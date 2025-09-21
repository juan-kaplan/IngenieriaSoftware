package org.udesa.tp1.model;

import java.time.LocalDateTime;
import java.util.Objects;

public record Transaction(float amount, LocalDateTime timestamp, String description) {
    public static String NegativeAmountError = "Amount must be positive";

    public Transaction(float amount, LocalDateTime timestamp, String description) {
        if (amount <= 0) throw new IllegalArgumentException(NegativeAmountError);
        this.amount = amount;
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
        this.description = description;
    }


}