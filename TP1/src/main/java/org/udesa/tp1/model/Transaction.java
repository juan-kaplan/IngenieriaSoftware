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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction other)) return false;
        return Float.compare(this.amount, other.amount) == 0
                && Objects.equals(this.timestamp, other.timestamp)
                && Objects.equals(this.description, other.description);
    }




}