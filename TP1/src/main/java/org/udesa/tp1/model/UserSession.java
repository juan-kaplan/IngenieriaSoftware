package org.udesa.tp1.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class UserSession {
    private LocalDateTime lastAccessed;
    private Clock clock;
    private String user;


    public UserSession(String user, LocalDateTime lastAccessed, Clock clock) {
        this.user = user;
        this.lastAccessed = lastAccessed;
        this.clock = clock;
    }

    public UserSession claimGiftCard(GiftCard giftCard) {
        giftCard.claimCard(user);
        lastAccessed = clock.now();
        return this;
    }

    public boolean isActive() {
        return 5 * 60 > Duration.between(lastAccessed, clock.now()).toSeconds();
    }

    public float checkBalance(GiftCard giftCard) {
        lastAccessed = clock.now();
        return giftCard.balance();
    }

    public List<Transaction> checkExpenses(GiftCard giftCard) {
        lastAccessed =  clock.now();
        return giftCard.expenses();
    }
}
