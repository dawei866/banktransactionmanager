package com.example.banktransactionmanager.repository;

import com.example.banktransactionmanager.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Transaction depositTransaction;
    private Transaction withdrawalTransaction;

    @BeforeEach
    void setUp() {
        // 创建测试数据
        depositTransaction = Transaction.builder()
                .transactionType(Transaction.TransactionType.DEPOSIT)
                .status(Transaction.Status.SUCCESS)
                .amount(BigDecimal.valueOf(1000.00))
                .currency("CNY")
                .description("Initial deposit")
                .transactionTime(LocalDateTime.now().minusDays(1))
                .accountNumber("1234567890123456")
                .accountType(Transaction.AccountType.SAVINGS)
                .channel(Transaction.Channel.APP)
                .externalReferenceNo("REF123")
                .referenceNumber("REF123")
                .fee(BigDecimal.ZERO)
                .transactionBizNo("20240101APP" + generateRandomString(8))
                .isDeleted(false)
                .createTime(LocalDateTime.now())
                .creator("system")
                .build();
        entityManager.persist(depositTransaction);

        withdrawalTransaction = Transaction.builder()
                .transactionType(Transaction.TransactionType.WITHDRAWAL)
                .status(Transaction.Status.SUCCESS)
                .amount(BigDecimal.valueOf(200.00))
                .currency("CNY")
                .description("Cash withdrawal")
                .transactionTime(LocalDateTime.now())
                .accountNumber("1234567890123456")
                .accountType(Transaction.AccountType.SAVINGS)
                .channel(Transaction.Channel.COUNTER)
                .externalReferenceNo("REF456")
                .referenceNumber("REF456")
                .fee(BigDecimal.valueOf(2.00))
                .transactionBizNo("20240101CTR" + generateRandomString(8))
                .isDeleted(false)
                .createTime(LocalDateTime.now())
                .creator("system")
                .build();
        entityManager.persist(withdrawalTransaction);

        // 创建另一个账户的交易
        Transaction otherAccountTransaction = Transaction.builder()
                .transactionType(Transaction.TransactionType.DEPOSIT)
                .status(Transaction.Status.SUCCESS)
                .amount(BigDecimal.valueOf(500.00))
                .currency("CNY")
                .description("Transfer from another account")
                .transactionTime(LocalDateTime.now())
                .accountNumber("9876543210987654")
                .accountType(Transaction.AccountType.CHECKING)
                .counterpartyAccountNumber("1234567890123456")
                .counterpartyName("Test User")
                .channel(Transaction.Channel.ONLINE_BANK)
                .externalReferenceNo("REF789")
                .referenceNumber("REF789")
                .fee(BigDecimal.ZERO)
                .transactionBizNo("20240101WEB" + generateRandomString(8))
                .isDeleted(false)
                .createTime(LocalDateTime.now())
                .creator("system")
                .build();
        entityManager.persist(otherAccountTransaction);

        // 创建一个已删除的交易
        Transaction deletedTransaction = Transaction.builder()
                .transactionType(Transaction.TransactionType.TRANSFER)
                .status(Transaction.Status.SUCCESS)
                .amount(BigDecimal.valueOf(300.00))
                .currency("CNY")
                .description("Transfer to another account")
                .transactionTime(LocalDateTime.now())
                .accountNumber("1234567890123456")
                .accountType(Transaction.AccountType.SAVINGS)
                .channel(Transaction.Channel.THIRD_PARTY)
                .externalReferenceNo("REF999")
                .fee(BigDecimal.valueOf(1.00))
                .transactionBizNo("20240101MOB" + generateRandomString(8))
                .isDeleted(true)
                .deletedTime(LocalDateTime.now())
                .createTime(LocalDateTime.now())
                .creator("system")
                .build();
        entityManager.persist(deletedTransaction);

        entityManager.flush();
    }

    private String generateRandomString(int length) {
        // 简单实现，生成随机字符串用于业务编号
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }

    @Test
    void testFindByIsDeletedFalse() {
        // 执行
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> transactions = transactionRepository.findAllByIsDeletedFalse(pageable);

        // 验证
        assertNotNull(transactions);
        // 应该只有3个交易是未删除的（depositTransaction, withdrawalTransaction, otherAccountTransaction）
        assertEquals(3, transactions.getTotalElements());
        assertTrue(transactions.getContent().stream().noneMatch(Transaction::getIsDeleted));
    }

    @Test
    void testSoftDeleteById() {
        // 执行软删除
        transactionRepository.softDeleteById(depositTransaction.getId());

        // 刷新实体管理器以确保更改生效
        entityManager.flush();
        entityManager.clear();

        // 验证
        Optional<Transaction> deletedTransaction = transactionRepository.findById(depositTransaction.getId());
        assertTrue(deletedTransaction.isPresent());
        assertTrue(deletedTransaction.get().getIsDeleted());
        assertNotNull(deletedTransaction.get().getDeletedTime());
    }

    @Test
    void testFindByTransactionBizNo() {
        // 执行
        Optional<Transaction> foundTransaction = transactionRepository.findByTransactionBizNo(depositTransaction.getTransactionBizNo());

        // 验证
        assertTrue(foundTransaction.isPresent());
        assertEquals(depositTransaction.getId(), foundTransaction.get().getId());
    }

    @Test
    void testFindByReferenceNumberAndIsDeletedFalse() {
        // 执行
        Optional<Transaction> foundTransaction = transactionRepository.findByReferenceNumberAndIsDeletedFalse("REF123");

        // 验证
        assertTrue(foundTransaction.isPresent());
        assertEquals(depositTransaction.getId(), foundTransaction.get().getId());
        assertEquals("REF123", foundTransaction.get().getReferenceNumber());
    }
}