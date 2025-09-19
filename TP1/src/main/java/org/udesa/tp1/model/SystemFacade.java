package org.udesa.tp1.model;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SystemFacade {
    public static String InvalidLoginCredentialsError = "Invalid Login Credentials";
    public static String InvalidGiftCardSelectedError =  "Invalid Gift Card Selected";
    public static String InvalidSessionError = "Invalid Session";

    private Map<String, String> validUsers;
    private Map<Integer, GiftCard> validGiftCards;
    private Map<Integer, UserSession> activeSessions;
    private int tokenNum;
    private Clock clock;

    public SystemFacade(Map<String, String> validUsers, Map<Integer, GiftCard> validGiftCards, Clock clock) {
        this.validUsers = validUsers;
        this.validGiftCards = validGiftCards;
        this.activeSessions = new HashMap<>();
        this.clock = clock;
        this.tokenNum = 0;
    }

    public Integer login(String user, String password) {
        if (!validUsers.containsKey(user) || !validUsers.get(user).equals(password))
            throw new RuntimeException(InvalidLoginCredentialsError);

        activeSessions.put(++tokenNum, new UserSession(user, clock.now(), clock));
        return tokenNum;
    }

    public boolean isTokenValid(Integer token) {
        return activeSessions.containsKey(token) || activeSessions.get(token).isActive();
    }

    public SystemFacade claimGiftCard(Integer token, Integer giftCardId) {
        validateTokenAndGiftCardInformation(token, giftCardId);

        activeSessions.get(token).claimGiftCard(validGiftCards.get(giftCardId));
        return this;
    }

    public float checkBalance(Integer token, Integer giftCardId) {
        validateTokenAndGiftCardInformation(token, giftCardId);

        return activeSessions.get(token).checkBalance(validGiftCards.get(giftCardId));
    }

    private void validateTokenAndGiftCardInformation(Integer token, Integer giftCardId) {
        if (!isTokenValid(token))
            throw new RuntimeException(InvalidSessionError);

        if (!validGiftCards.containsKey(giftCardId))
            throw new RuntimeException(InvalidGiftCardSelectedError);
    }
}
