package com.example.banktransactionmanager.repository;

import com.example.banktransactionmanager.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * 根据交易业务编号查询交易记录
     */
    Optional<Transaction> findByTransactionBizNo(String transactionBizNo);

    /**
     * 检查交易业务编号是否存在
     */
    boolean existsByTransactionBizNo(String transactionBizNo);

    /**
     * 查询所有未删除的交易记录
     */
    Page<Transaction> findByIsDeletedFalse(Pageable pageable);

    /**
     * 查询所有未删除的交易记录，支持筛选条件
     */
    @Query("SELECT t FROM Transaction t WHERE t.isDeleted = false " +
           "AND (:accountNumber IS NULL OR t.accountNumber = :accountNumber) " +
           "AND (:transactionType IS NULL OR t.transactionType = :transactionType) " +
           "AND (:status IS NULL OR t.status = :status)")
    Page<Transaction> findByIsDeletedFalseAndFilters(
        Pageable pageable,
        @Param("accountNumber") String accountNumber,
        @Param("transactionType") Transaction.TransactionType transactionType,
        @Param("status") Transaction.Status status);
    
    /**
     * 查询未删除的交易记录，支持分页
     */
    @Query("SELECT t FROM Transaction t WHERE t.isDeleted = false")
    Page<Transaction> findAllByIsDeletedFalse(Pageable pageable);

    /**
     * 软删除交易记录
     */
    @Modifying
    @Transactional
    @Query("UPDATE Transaction t SET t.isDeleted = true, t.deletedTime = CURRENT_TIMESTAMP WHERE t.id = :id AND t.isDeleted = false")
    void softDeleteById(@Param("id") Long id);
    
    /**
     * 根据金额范围查询未删除的交易记录
     */
    @Query("SELECT t FROM Transaction t WHERE t.amount BETWEEN :minAmount AND :maxAmount AND t.isDeleted = false")
    Page<Transaction> findByAmountBetweenAndIsDeletedFalse(
        @Param("minAmount") BigDecimal minAmount,
        @Param("maxAmount") BigDecimal maxAmount,
        Pageable pageable);
        
    /**
     * 根据描述关键词搜索未删除的交易记录
     */
    @Query("SELECT t FROM Transaction t WHERE LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')) AND t.isDeleted = false")
    Page<Transaction> findByDescriptionContainingIgnoreCaseAndIsDeletedFalse(
        @Param("keyword") String keyword,
        Pageable pageable);
        
    /**
     * 根据参考号查询未删除的交易记录
     */
    @Query("SELECT t FROM Transaction t WHERE t.referenceNumber = :referenceNumber AND t.isDeleted = false")
    Optional<Transaction> findByReferenceNumberAndIsDeletedFalse(@Param("referenceNumber") String referenceNumber);
}