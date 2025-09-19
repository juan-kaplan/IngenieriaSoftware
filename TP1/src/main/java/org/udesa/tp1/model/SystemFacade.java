package org.udesa.tp1.model;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SystemFacade {
    public static String InvalidLoginCredentialsError = "Invalid Login Credentials";

    private Map<String, String> validUsers;
    private List<GiftCard> validGiftCards;
    private Map<Integer, UserSession> activeSessions;

    public SystemFacade(Map<String, String> validUsers, List<GiftCard> validGiftCards) {
        this.validUsers = validUsers;
        this.validGiftCards = validGiftCards;
        this.activeSessions = new HashMap<>();
    }

    public Integer login(String user, String password) {
        if (!validUsers.getOrDefault(user, "").equals(password))
            throw new RuntimeException(InvalidLoginCredentialsError);

        Integer token = ThreadLocalRandom.current().nextInt();
        activeSessions.put(token, new UserSession(user));
        return token;
    }

    public boolean isTokenValid(Integer token) {
        return activeSessions.containsKey(token);
    }
}
