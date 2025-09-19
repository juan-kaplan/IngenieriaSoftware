package org.udesa.tp1.model;

import java.time.Duration;
import java.time.LocalDateTime;

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
        giftCard.setOwner(user);
        return this;
    }

    public boolean isActive() {
        return 5 * 60 > Duration.between(lastAccessed, LocalDateTime.now()).toSeconds();
    }

    public float checkBalance(GiftCard giftCard) {
        return giftCard.balance();
    }
}
