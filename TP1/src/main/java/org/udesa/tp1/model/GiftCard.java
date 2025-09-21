package org.udesa.tp1.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GiftCard{
    public static String NotEnoughBalanceError = "Gift Card has not enough balance";
    public static String GiftCardIsClaimedError = "Gift Card is claimed";
    public static String GiftCardIsNotClaimedError = "Operation invalid: Gift Card is not claimed";
    public static String UserDoesNotOwnCardError = "The selected User doesn't own the Card";

    private float balance;
    private final List<Transaction> expenses;
    private String owner;
    private boolean isClaimed;

    public GiftCard(float balance) {
        this.balance = balance;
        this.expenses = new ArrayList<>();
        this.isClaimed = false;
        this.owner = null;
    }

    public List<Transaction> expenses() {
        return Collections.unmodifiableList(expenses);
    }

    public float balance(){
        return balance;
    }

    private GiftCard addTransaction(float amount, LocalDateTime timestamp, String storeName) {
        expenses.add(new  Transaction(amount, timestamp, storeName));
        return this;
    }

    public String owner() {
        if (!isClaimed())
            throw new  RuntimeException(GiftCardIsNotClaimedError);
        return owner;
    }

    public GiftCard claimCard(String owner) {
        if  (isClaimed())
            throw new RuntimeException(GiftCardIsClaimedError);
        this.isClaimed = true;
        this.owner = owner;
        return this;
    }

    public GiftCard chargeGiftCard(String user, float amount, String paymentDescription, LocalDateTime now) {
        if (!Objects.equals(owner(), user))
            throw new RuntimeException(UserDoesNotOwnCardError);
        if (amount > balance)
            throw new RuntimeException(NotEnoughBalanceError);

        balance -= amount;
        return addTransaction(amount, now, paymentDescription);
    }

    public boolean isClaimed() {return isClaimed;}
}
