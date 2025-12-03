package com.example.banktransactionmanager.controller;

import com.example.banktransactionmanager.exception.BusinessException;
import com.example.banktransactionmanager.model.Transaction;
import com.example.banktransactionmanager.model.dto.TransactionCreateRequest;
import com.example.banktransactionmanager.model.dto.TransactionDTO;
import com.example.banktransactionmanager.model.dto.TransactionUpdateRequest;
import com.example.banktransactionmanager.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * 创建新交易
     * @param request 交易信息
     * @return 是否创建成功
     */
    @PostMapping
    public ResponseEntity<String> createTransaction(@Valid @RequestBody TransactionCreateRequest request) {
        try {
            transactionService.createTransaction(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Transaction created successfully");
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create transaction: " + e.getMessage());
        }
    }

    /**
     * 获取所有交易，支持分页和筛选条件
     * @param pageable 分页参数
     * @param accountNumber 账号筛选条件（可选）
     * @param transactionType 交易类型筛选条件（可选）
     * @param status 状态筛选条件（可选）
     * @return 分页后的交易列表
     */
    @GetMapping
    public ResponseEntity<Page<TransactionDTO>> getAllTransactions(
            Pageable pageable,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) Transaction.TransactionType transactionType,
            @RequestParam(required = false) Transaction.Status status) {
        Page<TransactionDTO> transactions = transactionService.getAllTransactions(pageable, accountNumber, transactionType, status);
        return ResponseEntity.ok(transactions);
    }

    /**
     * 根据ID更新交易信息
     * @param id 交易ID
     * @param request 新的交易信息
     * @return 是否更新成功
     */
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateTransaction(@PathVariable Long id, 
                                                   @Valid @RequestBody TransactionUpdateRequest request) {
        try {
            transactionService.updateTransaction(id, request);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    /**
     * 软删除交易
     * @param id 交易ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> softDeleteTransaction(@PathVariable Long id) {
        try {
            transactionService.softDeleteTransaction(id);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
}