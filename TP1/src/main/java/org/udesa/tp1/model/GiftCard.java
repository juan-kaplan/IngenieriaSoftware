package org.udesa.tp1.model;

public class GiftCard {
    public static String NotEnoughBalanceError = "Gift Card has not enough balance";
    public static String GiftCardIsClaimedError = "Gift Card is claimed";

    private float balance;
    private String expenses;
    private String owner;

    public GiftCard(float balance) {
        this.balance = balance;
        this.expenses = "";
        this.owner = null;
    }

    public String expenses() {
        return expenses;
    }

    public float balance(){
        return balance;
    }

    public GiftCard spend(int amount) {
        if (amount > balance)
            throw new RuntimeException(NotEnoughBalanceError);

        this.balance -= amount;
        return this;
    }

    public String owner() {
        return owner;
    }

    public GiftCard setOwner(String owner) {
        if  (this.owner != null)
            throw new RuntimeException(GiftCardIsClaimedError);
        this.owner = owner;
        return this;
    }
}
