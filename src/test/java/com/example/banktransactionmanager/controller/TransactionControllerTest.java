package com.example.banktransactionmanager.controller;

import com.example.banktransactionmanager.model.Transaction;
import com.example.banktransactionmanager.model.dto.TransactionCreateRequest;
import com.example.banktransactionmanager.model.dto.TransactionDTO;
import com.example.banktransactionmanager.model.dto.TransactionUpdateRequest;
import com.example.banktransactionmanager.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private TransactionDTO transactionDTO;

    @BeforeEach
    void setUp() {
        transactionDTO = new TransactionDTO();
        transactionDTO.setId(1L);
        transactionDTO.setTransactionBizNo("20240101APP12345678");
        transactionDTO.setTransactionType(Transaction.TransactionType.DEPOSIT);
        transactionDTO.setStatus(Transaction.Status.SUCCESS);
        transactionDTO.setAmount(BigDecimal.valueOf(1000.00));
        transactionDTO.setCurrency("CNY");
        transactionDTO.setDescription("Test deposit");
        transactionDTO.setTransactionTime(LocalDateTime.now());
        transactionDTO.setAccountNumber("1234567890123456");
        transactionDTO.setAccountType(Transaction.AccountType.SAVINGS);
        transactionDTO.setChannel(Transaction.Channel.APP);
        transactionDTO.setExternalReferenceNo("TEST123");
        transactionDTO.setFee(BigDecimal.ZERO);
        transactionDTO.setRemarks("Test remarks");
        transactionDTO.setIsDeleted(false);
        transactionDTO.setCreateTime(LocalDateTime.now());
        transactionDTO.setCreator("system");
    }

    @Test
    void testCreateTransaction_InvalidInput() throws Exception {
        // 准备
        TransactionCreateRequest request = new TransactionCreateRequest();
        request.setTransactionType(null); // Invalid input
        request.setAmount(BigDecimal.valueOf(-100)); // Invalid input

        // 执行 & 验证
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateTransaction_Success() throws Exception {
        // 准备
        TransactionCreateRequest request = new TransactionCreateRequest();
        request.setTransactionType(Transaction.TransactionType.DEPOSIT);
        request.setAmount(BigDecimal.valueOf(1000.00));
        request.setDescription("Test deposit");
        request.setAccountNumber("1234567890123456");
        request.setAccountType(Transaction.AccountType.SAVINGS);
        request.setChannel(Transaction.Channel.APP);
        request.setExternalReferenceNo("TEST123");
        request.setFee(BigDecimal.ZERO);
        request.setCreator("testUser");
        
        when(transactionService.createTransaction(any(TransactionCreateRequest.class))).thenReturn(true);

        // 执行 & 验证
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Transaction created successfully"));
    }

    @Test
    void testGetAllTransactions() throws Exception {
        // 准备
        List<TransactionDTO> transactions = Collections.singletonList(transactionDTO);
        Page<TransactionDTO> page = new PageImpl<>(transactions, PageRequest.of(0, 10), 1);
        when(transactionService.getAllTransactions(any(Pageable.class), isNull(String.class), isNull(Transaction.TransactionType.class), isNull(Transaction.Status.class))).thenReturn(page);

        // 执行 & 验证
        mockMvc.perform(get("/api/transactions?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testUpdateTransaction_Success() throws Exception {
        // 准备
        Long transactionId = 1L;
        TransactionUpdateRequest request = new TransactionUpdateRequest();
        request.setTransactionType(Transaction.TransactionType.WITHDRAWAL);
        request.setStatus(Transaction.Status.SUCCESS);
        request.setAmount(BigDecimal.valueOf(1000.00));
        request.setCurrency("CNY");
        request.setDescription("Updated withdrawal");
        request.setAccountNumber("1234567890123456");
        request.setAccountType(Transaction.AccountType.SAVINGS);
        request.setChannel(Transaction.Channel.APP);
        request.setExternalReferenceNo("TEST123");
        request.setFee(BigDecimal.ZERO);
        request.setUpdater("processor");

        doNothing().when(transactionService).updateTransaction(eq(transactionId), any(TransactionUpdateRequest.class));

        // 执行 & 验证
        mockMvc.perform(put("/api/transactions/{id}", transactionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testSoftDeleteTransaction_Success() throws Exception {
        // 准备
        Long transactionId = 1L;
        
        doNothing().when(transactionService).softDeleteTransaction(transactionId);

        // 执行 & 验证
        mockMvc.perform(delete("/api/transactions/{id}", transactionId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}