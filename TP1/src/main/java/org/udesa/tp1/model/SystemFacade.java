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
            if (!Objects.equals(validUsers.get(user), password))
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
            getActiveSession(token).claimGiftCard(getValidGiftCard(giftCardId));
            return this;
        }

        public float checkBalance(Integer token, Integer giftCardId) {
            return getActiveSession(token).checkBalance(getValidGiftCard(giftCardId));
        }

        public List<Transaction> checkExpenses(Integer token, Integer giftCardId) {
            return getActiveSession(token).checkExpenses(getValidGiftCard(giftCardId));
        }

        public SystemFacade chargeUsersGiftCard(String merchant, Integer giftCardId, String user, float amount, String payment_description){
            assessUserName(user);
            assessMerchant(merchant);
            getValidGiftCard(giftCardId).chargeGiftCard(user, amount, payment_description, clock.now());

            return this;
        }

        private UserSession getActiveSession(Integer token) {
            if (!isTokenValid(token))
                throw new RuntimeException(InvalidSessionError);

            return activeSessions.get(token);
        }

        private GiftCard getValidGiftCard(Integer giftCardId){
            if (!validGiftCards.containsKey(giftCardId))
                throw new RuntimeException(InvalidGiftCardSelectedError);

            return validGiftCards.get(giftCardId);
        }

        private void assessUserName(String user){
            if (!validUsers.containsKey(user))
                throw new RuntimeException(InvalidUserNameError);
        }

        private void assessMerchant(String merchant){
            if (!validMerchants.contains(merchant))
                throw new RuntimeException(InvalidMerchantError);
        }


    }
