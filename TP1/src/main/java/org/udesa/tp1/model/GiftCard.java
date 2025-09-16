package org.udesa.tp1.model;

public class GiftCard {
    public static String NotEnoughBalanceErrorDescription = "Gift Card has not enough balance";

    private float balance;
    private String expenses;

    public GiftCard(float balance) {
        this.balance = balance;
        this.expenses = "";
    }

    public String expenses() {
        return expenses;
    }

    public float balance(){
        return balance;
    }

    public GiftCard spend(int amount) {
        if (amount > balance)
            throw new RuntimeException(NotEnoughBalanceErrorDescription);

        this.balance -= amount;
        return this;
    }
}
