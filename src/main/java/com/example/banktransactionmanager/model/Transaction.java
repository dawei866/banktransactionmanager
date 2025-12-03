package com.example.banktransactionmanager.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction", indexes = {
        @Index(name = "idx_transaction_biz_no", columnList = "transaction_biz_no", unique = true),
        @Index(name = "idx_account_number", columnList = "account_number"),
        @Index(name = "idx_transaction_time", columnList = "transaction_time")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Transaction business number is required")
    @Size(max = 64, message = "Transaction business number must not exceed 64 characters")
    @Column(name = "transaction_biz_no", nullable = false, unique = true)
    private String transactionBizNo;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Transaction type is required")
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    @Column(nullable = false)
    private Status status;

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

    @NotNull(message = "Transaction time is required")
    @Column(name = "transaction_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transactionTime;

    @NotBlank(message = "Account number is required")
    @Pattern(regexp = "^[0-9]{10,20}$", message = "Account number must be 10-20 digits")
    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Account type is required")
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Pattern(regexp = "^[0-9]{10,20}$", message = "Counterparty account number must be 10-20 digits")
    @Column(name = "counterparty_account_number")
    private String counterpartyAccountNumber;

    @Size(max = 100, message = "Counterparty name must not exceed 100 characters")
    @Column(name = "counterparty_name")
    private String counterpartyName;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Channel is required")
    private Channel channel;

    @Size(max = 64, message = "External reference number must not exceed 64 characters")
    @Column(name = "external_reference_no")
    private String externalReferenceNo;

    @NotNull(message = "Fee is required")
    @Min(value = 0, message = "Fee must be greater than or equal to zero")
    @Digits(integer = 8, fraction = 2, message = "Fee must have up to 8 digits before decimal point and 2 digits after")
    private BigDecimal fee;

    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;

    @Size(max = 50, message = "Reference number must not exceed 50 characters")
    @Column(name = "reference_number")
    private String referenceNumber;

    @NotNull(message = "Is deleted flag is required")
    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "deleted_time")
    private LocalDateTime deletedTime;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @NotBlank(message = "Creator is required")
    @Size(max = 50, message = "Creator must not exceed 50 characters")
    private String creator;

    @Size(max = 50, message = "Updater must not exceed 50 characters")
    private String updater;

    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER,
        PAYMENT,
        REFUND
    }

    public enum Status {
        PENDING,
        SUCCESS,
        FAILED,
        CANCELLED
    }

    public enum AccountType {
        SAVINGS,
        CHECKING,
        CREDIT,
        CORPORATE
    }

    public enum Channel {
        APP,
        ONLINE_BANK,
        COUNTER,
        THIRD_PARTY
    }
}