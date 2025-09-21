    package org.udesa.tp1.model;

    import java.time.format.DateTimeFormatter;
    import java.util.*;

    public class SystemFacade {
        public static String InvalidLoginCredentialsError = "Invalid Login Credentials";
        public static String InvalidGiftCardSelectedError =  "Invalid Gift Card Selected";
        public static String InvalidSessionError = "Invalid Session";
        public static String InvalidUserNameError = "Invalid User Name";
        public static String InvalidMerchantError = "Invalid Merchant";

        private Map<String, String> validUsers;
        private Map<Integer, GiftCard> validGiftCards;
        private Map<Integer, UserSession> activeSessions;
        private List<String> validMerchants;
        private int tokenNum;
        private Clock clock;

        public SystemFacade(Map<String, String> validUsers, Map<Integer, GiftCard> validGiftCards, List<String> validMerchants, Clock clock) {
            this.validUsers = validUsers;
            this.validGiftCards = validGiftCards;
            this.activeSessions = new HashMap<>();
            this.validMerchants = validMerchants;
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
            if (!activeSessions.containsKey(token))
                return false;

            if (activeSessions.get(token).isActive())
                return true;

            activeSessions.remove(token);
            return false;
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

        public SystemFacade chargeUsersGiftCard(String merchant, Integer giftCardId, String user, float amount, String payment_description){
            validateMerchantAndUserAndGiftCardInformation(merchant, giftCardId, user);
            validGiftCards.get(giftCardId).chargeGiftCard(user, amount, payment_description, clock.now());

            return this;
        }

        private void validateMerchantAndUserAndGiftCardInformation(String merchant, Integer giftCardId, String user) {
            if (!validUsers.containsKey(user))
                throw new RuntimeException(InvalidUserNameError);

            if (!validMerchants.contains(merchant))
                throw new RuntimeException(InvalidMerchantError);

            if (!validGiftCards.containsKey(giftCardId))
                throw new RuntimeException(InvalidGiftCardSelectedError);


        }


        private void validateTokenAndGiftCardInformation(Integer token, Integer giftCardId) {
            if (!isTokenValid(token))
                throw new RuntimeException(InvalidSessionError);

            if (!validGiftCards.containsKey(giftCardId))
                throw new RuntimeException(InvalidGiftCardSelectedError);
        }
    }
