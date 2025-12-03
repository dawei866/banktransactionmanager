package com.example.banktransactionmanager.service.impl;

import com.example.banktransactionmanager.exception.BusinessException;
import com.example.banktransactionmanager.exception.ResourceNotFoundException;
import com.example.banktransactionmanager.model.Transaction;
import com.example.banktransactionmanager.model.dto.TransactionCreateRequest;
import com.example.banktransactionmanager.model.dto.TransactionDTO;
import com.example.banktransactionmanager.model.dto.TransactionUpdateRequest;
import com.example.banktransactionmanager.repository.TransactionRepository;
import com.example.banktransactionmanager.service.TransactionService;
import com.example.banktransactionmanager.util.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final Random random = new Random();
    private final SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(1, 1);

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public boolean createTransaction(TransactionCreateRequest request) {
        // 最多重试3次来处理并发情况下可能的ID冲突
        for (int i = 0; i < 3; i++) {
            try {
                // 生成唯一的交易业务编号
                String transactionBizNo = generateTransactionBizNo(request.getChannel());
                
                // 检查是否已存在相同的交易业务编号
                if (transactionRepository.existsByTransactionBizNo(transactionBizNo)) {
                    // 如果已存在，则继续下一次循环重试
                    continue;
                }
                
                // 构建Transaction对象
                Transaction transaction = new Transaction();
                transaction.setTransactionBizNo(transactionBizNo);
                transaction.setAccountNumber(request.getAccountNumber());
                transaction.setAmount(request.getAmount());
                transaction.setTransactionType(request.getTransactionType());
                transaction.setTransactionTime(request.getTransactionTime());
                transaction.setStatus(Transaction.Status.PENDING);
                transaction.setDescription(request.getDescription());
                transaction.setChannel(request.getChannel());
                
                // 保存到数据库
                transactionRepository.save(transaction);
                return true; // 成功保存后直接返回
            } catch (Exception e) {
                // 如果是最后一次尝试，抛出业务异常
                if (i == 2) {
                    throw new BusinessException("Failed to create transaction after 3 attempts due to ID conflicts", e);
                }
                // 其他情况继续下一次循环重试
            }
        }
        
        // 如果3次尝试后仍未成功，抛出业务异常
        throw new BusinessException("Failed to create transaction after 3 attempts due to ID conflicts");
    }

    @Override
    @Cacheable(value = "transactions", key = "#id")
    public Optional<TransactionDTO> getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .filter(t -> !t.getIsDeleted())
                .map(this::convertToDTO);
    }

    @Override
    @CacheEvict(value = {"transactions", "transactionList", "accountBalance"}, key = "#id")
    public void updateTransaction(Long id, TransactionUpdateRequest request) {
        // 查找现有的交易记录
        Transaction existingTransaction = transactionRepository.findById(id)
                .filter(t -> !t.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        // 更新交易记录的属性
        existingTransaction.setTransactionType(request.getTransactionType());
        existingTransaction.setStatus(request.getStatus());
        existingTransaction.setAmount(request.getAmount());
        existingTransaction.setCurrency(request.getCurrency());
        existingTransaction.setDescription(request.getDescription());
        existingTransaction.setAccountNumber(request.getAccountNumber());
        existingTransaction.setAccountType(request.getAccountType());
        existingTransaction.setCounterpartyAccountNumber(request.getCounterpartyAccountNumber());
        existingTransaction.setCounterpartyName(request.getCounterpartyName());
        existingTransaction.setChannel(request.getChannel());
        existingTransaction.setExternalReferenceNo(request.getExternalReferenceNo());
        existingTransaction.setFee(request.getFee());
        existingTransaction.setRemarks(request.getRemarks());
        existingTransaction.setUpdateTime(LocalDateTime.now());
        existingTransaction.setUpdater(request.getUpdater() != null ? request.getUpdater() : "SYSTEM");

        // 保存更新后的交易记录
        transactionRepository.save(existingTransaction);
    }

    @Override
    @CacheEvict(value = {"transactions", "transactionList", "accountBalance"}, allEntries = true)
    public void softDeleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .filter(t -> !t.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        
        transactionRepository.softDeleteById(id);
    }

    @Override
    @Cacheable(value = "transactionList", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<TransactionDTO> getAllTransactions(Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByIsDeletedFalseAndFilters(pageable, null, null, null);
        return transactions.map(this::convertToDTO);
    }

    @Override
    @Cacheable(value = "transactionList", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #accountNumber + '-' + #transactionType + '-' + #status")
    public Page<TransactionDTO> getAllTransactions(Pageable pageable, String accountNumber, Transaction.TransactionType transactionType, Transaction.Status status) {
        Page<Transaction> transactions = transactionRepository.findByIsDeletedFalseAndFilters(pageable, accountNumber, transactionType, status);
        return transactions.map(this::convertToDTO);
    }

    @Override
    @Cacheable(value = "transactions", key = "#transactionBizNo")
    public Optional<TransactionDTO> getTransactionByTransactionBizNo(String transactionBizNo) {
        return transactionRepository.findByTransactionBizNo(transactionBizNo)
                .filter(t -> !t.getIsDeleted())
                .map(this::convertToDTO);
    }

    @Override
    @Cacheable(value = "transactionList", key = "#minAmount + '-' + #maxAmount + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<TransactionDTO> getTransactionsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByAmountBetweenAndIsDeletedFalse(minAmount, maxAmount, pageable);
        return transactions.map(this::convertToDTO);
    }

    @Override
    @Cacheable(value = "transactionList", key = "#keyword + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<TransactionDTO> searchTransactionsByKeyword(String keyword, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByDescriptionContainingIgnoreCaseAndIsDeletedFalse(keyword, pageable);
        return transactions.map(this::convertToDTO);
    }

    // 辅助方法：生成交易业务编号
    private String generateTransactionBizNo(Transaction.Channel channel) {
        // 使用雪花算法生成唯一ID
        long uniqueId = snowflakeIdGenerator.nextId();
        String channelCode = channel != null ? channel.name() : "UNKNOWN"; // 使用枚举名称作为渠道编码
        return channelCode + uniqueId;
    }

    // 辅助方法：将实体对象转换为DTO
    private TransactionDTO convertToDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .transactionBizNo(transaction.getTransactionBizNo())
                .transactionType(transaction.getTransactionType())
                .status(transaction.getStatus())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .description(transaction.getDescription())
                .transactionTime(transaction.getTransactionTime())
                .accountNumber(transaction.getAccountNumber())
                .accountType(transaction.getAccountType())
                .counterpartyAccountNumber(transaction.getCounterpartyAccountNumber())
                .counterpartyName(transaction.getCounterpartyName())
                .channel(transaction.getChannel())
                .externalReferenceNo(transaction.getExternalReferenceNo())
                .fee(transaction.getFee())
                .remarks(transaction.getRemarks())
                .isDeleted(transaction.getIsDeleted())
                .deletedTime(transaction.getDeletedTime())
                .createTime(transaction.getCreateTime())
                .updateTime(transaction.getUpdateTime())
                .creator(transaction.getCreator())
                .updater(transaction.getUpdater())
                .build();
    }

    @Override
    @Cacheable(value = "transactions", key = "#referenceNumber")
    public Optional<TransactionDTO> getTransactionByReferenceNumber(String referenceNumber) {
        return transactionRepository.findByReferenceNumberAndIsDeletedFalse(referenceNumber)
                .map(this::convertToDTO);
    }
}