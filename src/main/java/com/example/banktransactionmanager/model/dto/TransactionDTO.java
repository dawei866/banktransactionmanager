package com.example.banktransactionmanager.model.dto;

import com.example.banktransactionmanager.model.Transaction;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {

    private Long id;

    @NotBlank(message = "Transaction business number is required")
    @Size(max = 64, message = "Transaction business number must not exceed 64 characters")
    private String transactionBizNo;

    @NotNull(message = "Transaction type is required")
    private Transaction.TransactionType transactionType;

    @NotNull(message = "Status is required")
    private Transaction.Status status;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be greater than zero")
    @Digits(integer = 14, fraction = 2, message = "Amount must have up to 14 digits before decimal point and 2 digits after")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3 uppercase letters")
    private String currency;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transactionTime;

    @NotBlank(message = "Account number is required")
    @Pattern(regexp = "^[0-9]{10,20}$", message = "Account number must be 10-20 digits")
    private String accountNumber;

    @NotNull(message = "Account type is required")
    private Transaction.AccountType accountType;

    @Pattern(regexp = "^[0-9]{10,20}$", message = "Counterparty account number must be 10-20 digits")
    private String counterpartyAccountNumber;

    @Size(max = 100, message = "Counterparty name must not exceed 100 characters")
    private String counterpartyName;

    @NotNull(message = "Channel is required")
    private Transaction.Channel channel;

    @Size(max = 64, message = "External reference number must not exceed 64 characters")
    private String externalReferenceNo;

    @NotNull(message = "Fee is required")
    @Min(value = 0, message = "Fee must be greater than or equal to zero")
    @Digits(integer = 8, fraction = 2, message = "Fee must have up to 8 digits before decimal point and 2 digits after")
    private BigDecimal fee;

    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;

    private Boolean isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @NotBlank(message = "Creator is required")
    @Size(max = 50, message = "Creator must not exceed 50 characters")
    private String creator;

    @Size(max = 50, message = "Updater must not exceed 50 characters")
    private String updater;
}