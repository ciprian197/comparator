package com.transact.comparator.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @JsonProperty("TransactionID")
    private String id;
    @JsonProperty("ProfileName")
    private String profileName;
    @JsonProperty("TransactionDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    @JsonProperty("TransactionAmount")
    private BigDecimal amount;
    @JsonProperty("TransactionNarrative")
    private String narrative;
    @JsonProperty("TransactionDescription")
    private String description;
    @JsonProperty("TransactionType")
    private int type;
    @JsonProperty("WalletReference")
    private String walletReference;

    public boolean isValid() {
        return id != null && !id.isBlank();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Transaction that)) return false;
        return type == that.type && Objects.equals(id, that.id)
                && Objects.equals(amount, that.amount) && Objects.equals(walletReference, that.walletReference)
                && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, amount, type, walletReference);
    }
}
