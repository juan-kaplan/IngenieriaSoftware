package org.udesa.tp1.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GiftCard implements Identifiable {
    public static String NotEnoughBalanceError = "Gift Card has not enough balance";
    public static String GiftCardIsClaimedError = "Gift Card is claimed";
    public static String GiftCardIsNotClaimedError = "Operation invalid: Gift Card is not claimed";


    private final String giftCardId;
    private float balance;
    private final List<Transaction> expenses;
    private String owner;
    private boolean isClaimed;

    public GiftCard(float balance, String giftCardId) {
        this.balance = balance;
        this.giftCardId = giftCardId;
        this.expenses = new ArrayList<>();
        this.isClaimed = false;
        this.owner = null;
    }

    public List<Transaction> expenses() {
        return Collections.unmodifiableList(expenses);
    }

    @Override
    public String id() {
        return giftCardId;
    }

    public float balance(){
        return balance;
    }

    public boolean isClaimed() {
        return isClaimed;
    }

    public GiftCard spend(float amount) {
        if (!isClaimed())
            throw new RuntimeException(GiftCardIsNotClaimedError);
        if (amount > balance)
            throw new RuntimeException(NotEnoughBalanceError);

        this.balance -= amount;
        return this;
    }

    public GiftCard addTransaction(Transaction transaction) {
        if (!isClaimed())
            throw new RuntimeException(GiftCardIsNotClaimedError);
        expenses.add(transaction);
        return this;
    }

    public String owner() {
        if (isClaimed())
            return owner;
        throw new  RuntimeException(GiftCardIsNotClaimedError);
    }


    public GiftCard claimCard(String owner) {
        if  (isClaimed())
            throw new RuntimeException(GiftCardIsClaimedError);
        this.isClaimed = true;
        this.owner = owner;
        return this;
    }
}
