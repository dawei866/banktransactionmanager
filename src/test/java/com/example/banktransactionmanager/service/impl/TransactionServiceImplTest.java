package com.example.banktransactionmanager.service.impl;

import com.example.banktransactionmanager.exception.BusinessException;
import com.example.banktransactionmanager.exception.ResourceNotFoundException;
import com.example.banktransactionmanager.model.Transaction;
import com.example.banktransactionmanager.model.dto.TransactionCreateRequest;
import com.example.banktransactionmanager.model.dto.TransactionDTO;
import com.example.banktransactionmanager.model.dto.TransactionUpdateRequest;
import com.example.banktransactionmanager.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.isNull;

class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Transaction transaction;
    private TransactionDTO transactionDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 初始化测试数据
        transaction = Transaction.builder()
                .id(1L)
                .transactionBizNo("20240101APP12345678")
                .transactionType(Transaction.TransactionType.DEPOSIT)
                .status(Transaction.Status.SUCCESS)
                .amount(BigDecimal.valueOf(1000.00))
                .currency("CNY")
                .description("Initial deposit")
                .transactionTime(LocalDateTime.now())
                .accountNumber("1234567890123456")
                .accountType(Transaction.AccountType.SAVINGS)
                .channel(Transaction.Channel.APP)
                .externalReferenceNo("REF123")
                .fee(BigDecimal.ZERO)
                .remarks("Test remarks")
                .isDeleted(false)
                .createTime(LocalDateTime.now())
                .creator("system")
                .build();

        transactionDTO = TransactionDTO.builder()
                .id(1L)
                .transactionBizNo("20240101APP12345678")
                .transactionType(Transaction.TransactionType.DEPOSIT)
                .status(Transaction.Status.SUCCESS)
                .amount(BigDecimal.valueOf(1000.00))
                .currency("CNY")
                .description("Initial deposit")
                .transactionTime(LocalDateTime.now())
                .accountNumber("1234567890123456")
                .accountType(Transaction.AccountType.SAVINGS)
                .channel(Transaction.Channel.APP)
                .externalReferenceNo("REF123")
                .fee(BigDecimal.ZERO)
                .remarks("Test remarks")
                .isDeleted(false)
                .createTime(LocalDateTime.now())
                .creator("system")
                .build();
    }

    @Test
    void testCreateTransaction_Success() {
        // 准备
        TransactionCreateRequest request = TransactionCreateRequest.builder()
                .transactionType(Transaction.TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(1000.00))
                .description("Initial deposit")
                .accountNumber("1234567890123456")
                .accountType(Transaction.AccountType.SAVINGS)
                .channel(Transaction.Channel.APP)
                .externalReferenceNo("REF123")
                .fee(BigDecimal.ZERO)
                .creator("system")
                .build();
        
        when(transactionRepository.existsByTransactionBizNo(anyString())).thenReturn(false);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // 执行
        transactionService.createTransaction(request);

        // 验证
        verify(transactionRepository).existsByTransactionBizNo(anyString());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testCreateTransaction_BusinessNumberConflict() {
        // 准备
        TransactionCreateRequest request = TransactionCreateRequest.builder()
                .transactionType(Transaction.TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(1000.00))
                .description("Initial deposit")
                .accountNumber("1234567890123456")
                .accountType(Transaction.AccountType.SAVINGS)
                .channel(Transaction.Channel.APP)
                .externalReferenceNo("REF123")
                .fee(BigDecimal.ZERO)
                .creator("system")
                .build();
        
        when(transactionRepository.existsByTransactionBizNo(anyString())).thenReturn(true);

        // 执行 & 验证
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> transactionService.createTransaction(request));
        assertEquals("Failed to create transaction after 3 attempts due to ID conflicts", exception.getMessage());
        verify(transactionRepository, times(3)).existsByTransactionBizNo(anyString());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testGetTransactionById_Success() {
        // 准备
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        // 执行
        Optional<TransactionDTO> result = transactionService.getTransactionById(1L);

        // 验证
        assertTrue(result.isPresent());
        assertEquals(transactionDTO.getId(), result.get().getId());
        verify(transactionRepository).findById(1L);
    }

    @Test
    void testGetTransactionById_NotFound() {
        // 准备
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行
        Optional<TransactionDTO> result = transactionService.getTransactionById(1L);

        // 验证
        assertFalse(result.isPresent());
        verify(transactionRepository).findById(1L);
    }

    @Test
    void testUpdateTransaction_Success() {
        // 准备
        Transaction updatedTransaction = Transaction.builder()
                .id(1L)
                .transactionBizNo("20240101APP12345678")
                .transactionType(Transaction.TransactionType.DEPOSIT)
                .status(Transaction.Status.SUCCESS)
                .amount(BigDecimal.valueOf(2000.00))
                .currency("CNY")
                .description("Updated deposit")
                .transactionTime(LocalDateTime.now())
                .accountNumber("1234567890123456")
                .accountType(Transaction.AccountType.SAVINGS)
                .channel(Transaction.Channel.APP)
                .externalReferenceNo("REF123")
                .fee(BigDecimal.ZERO)
                .remarks("Test remarks")
                .isDeleted(false)
                .createTime(LocalDateTime.now())
                .creator("system")
                .build();

        TransactionUpdateRequest request = TransactionUpdateRequest.builder()
                .transactionType(Transaction.TransactionType.DEPOSIT)
                .status(Transaction.Status.SUCCESS)
                .amount(BigDecimal.valueOf(2000.00))
                .currency("CNY")
                .description("Updated deposit")
                .transactionTime(LocalDateTime.now())
                .accountNumber("1234567890123456")
                .accountType(Transaction.AccountType.SAVINGS)
                .channel(Transaction.Channel.APP)
                .externalReferenceNo("REF123")
                .fee(BigDecimal.ZERO)
                .remarks("Test remarks")
                .updater("system")
                .build();

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(updatedTransaction);

        // 执行
        transactionService.updateTransaction(1L, request);

        // 验证
        verify(transactionRepository).findById(1L);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testSoftDeleteTransaction_Success() {
        // 准备
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        // 执行
        transactionService.softDeleteTransaction(1L);

        // 验证
        verify(transactionRepository).findById(1L);
        verify(transactionRepository).softDeleteById(1L);
    }

    @Test
    void testSoftDeleteTransaction_NotFound() {
        // 准备
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行 & 验证
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> transactionService.softDeleteTransaction(1L));
        assertEquals("Transaction not found with id: 1", exception.getMessage());
        verify(transactionRepository).findById(1L);
        verify(transactionRepository, never()).softDeleteById(any());
    }

    @Test
    void testGetAllTransactions() {
        // 准备
        List<Transaction> transactions = Collections.singletonList(transaction);
        Page<Transaction> page = new PageImpl<>(transactions, PageRequest.of(0, 10), 1);
        when(transactionRepository.findByIsDeletedFalseAndFilters(any(Pageable.class), isNull(String.class), isNull(Transaction.TransactionType.class), isNull(Transaction.Status.class))).thenReturn(page);

        // 执行
        Page<TransactionDTO> result = transactionService.getAllTransactions(PageRequest.of(0, 10), null, null, null);

        // 验证
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(transactionRepository).findByIsDeletedFalseAndFilters(any(Pageable.class), isNull(String.class), isNull(Transaction.TransactionType.class), isNull(Transaction.Status.class));
    }
}