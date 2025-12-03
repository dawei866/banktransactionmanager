package com.example.banktransactionmanager.service;

import com.example.banktransactionmanager.model.Transaction;
import com.example.banktransactionmanager.model.dto.TransactionCreateRequest;
import com.example.banktransactionmanager.model.dto.TransactionDTO;
import com.example.banktransactionmanager.model.dto.TransactionUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

public interface TransactionService {

    /**
     * 创建新交易
     * @param request 交易信息
     * @return 是否创建成功
     */
    boolean createTransaction(TransactionCreateRequest request);

    /**
     * 根据ID获取交易
     * @param id 交易ID
     * @return 交易对象，若不存在则返回Optional.empty()
     */
    Optional<TransactionDTO> getTransactionById(Long id);

    /**
     * 更新交易信息
     * @param id 交易ID
     * @param request 新的交易信息
     */
    void updateTransaction(Long id, TransactionUpdateRequest request);

    /**
     * 软删除交易
     * @param id 交易ID
     */
    void softDeleteTransaction(Long id);

    /**
     * 获取所有未删除的交易，支持分页
     * @param pageable 分页参数
     * @return 分页后的交易列表
     */
    Page<TransactionDTO> getAllTransactions(Pageable pageable);
    
    /**
     * 获取所有未删除的交易，支持分页和筛选条件
     * @param pageable 分页参数
     * @param accountNumber 账号筛选条件（可选）
     * @param transactionType 交易类型筛选条件（可选）
     * @param status 状态筛选条件（可选）
     * @return 分页后的交易列表
     */
    Page<TransactionDTO> getAllTransactions(Pageable pageable, String accountNumber, Transaction.TransactionType transactionType, Transaction.Status status);

    /**
     * 根据参考号查找交易
     * @param referenceNumber 参考号
     * @return 交易对象，若不存在则返回Optional.empty()
     */
    Optional<TransactionDTO> getTransactionByReferenceNumber(String referenceNumber);

    /**
     * 根据交易业务编号查找交易
     * @param transactionBizNo 交易业务编号
     * @return 交易对象，若不存在则返回Optional.empty()
     */
    Optional<TransactionDTO> getTransactionByTransactionBizNo(String transactionBizNo);

    /**
     * 根据金额范围查询交易
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @param pageable 分页参数
     * @return 分页后的交易列表
     */
    Page<TransactionDTO> getTransactionsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable);

    /**
     * 根据描述关键词搜索交易
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 分页后的交易列表
     */
    Page<TransactionDTO> searchTransactionsByKeyword(String keyword, Pageable pageable);
}