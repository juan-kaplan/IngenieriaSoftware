    package org.udesa.tp1.model;

    import java.time.format.DateTimeFormatter;
    import java.util.*;

    public class SystemFacade {
        public static String InvalidLoginCredentialsError = "Invalid Login Credentials";
        public static String InvalidGiftCardSelectedError =  "Invalid Gift Card Selected";
        public static String InvalidSessionError = "Invalid Session";
        public static String InvalidUserNameError = "Invalid User Name";
        public static String InvalidMerchantError = "Invalid Merchant";
        public static String UserDoesNotOwnCardError = "The selected User doesn't own the Card";


        private Map<String, String> validUsers; // username, password
        private Map<String, String> validMerchants; //Id, name
        private GiftCardRepository validGiftCards;
        private Map<Integer, UserSession> activeSessions; // token, UserSession
        private int tokenNum;
        private Clock clock;

        public SystemFacade(Map<String, String> validUsers, Map<String, String> validMerchants, GiftCardRepository validGiftCards, Clock clock) {
            this.validUsers = validUsers;
            this.validGiftCards = validGiftCards;
            this.activeSessions = new HashMap<>();
            this.validMerchants = validMerchants;
            this.clock = clock;
            this.tokenNum = 0;
        }

        public GiftCardRepository validGiftCards() {
            return validGiftCards;
        }

        public Map<String, String> validMerchants() {
            return validMerchants;
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

        public SystemFacade claimGiftCard(Integer token, String giftCardId) {
            validateTokenAndGiftCardInformation(token, giftCardId);

            activeSessions.get(token).claimGiftCard(validGiftCards.findById(giftCardId));
            return this;
        }

        public float checkGiftCardBalance(Integer token, String giftCardId) {
            validateTokenAndGiftCardInformation(token, giftCardId);

            return activeSessions.get(token).checkBalance(validGiftCards.findById(giftCardId));
        }

        public SystemFacade chargeUsersGiftCard(String merchantId, String giftCardId, String user, float amount){
            validateMerchantAndUserAndGiftCardInformation(merchantId, giftCardId, user);
            GiftCard giftCard = validGiftCards.findById(giftCardId);
            giftCard.spend(amount);
            String date = clock.now().toLocalDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String time = clock.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            String description = String.format("User %s spent %s at merchant %s on %s at %s. Merchant Id: %s", user, amount, validMerchants().get(merchantId), date, time, merchantId);
            giftCard.addTransaction(new Transaction(amount, merchantId, clock.now(), description));

            return this;
        }

        private void validateMerchantAndUserAndGiftCardInformation(String merchantId, String giftCardId, String user) {
            // Despues estaria bueno hacer el generico de GiftcardRepository
            if (!validUsers.containsKey(user))
                throw new RuntimeException(InvalidUserNameError);

            if (!validMerchants.containsKey(merchantId))
                throw new RuntimeException(InvalidMerchantError);

            if (!validGiftCards.existsById(giftCardId))
                throw new RuntimeException(InvalidGiftCardSelectedError);

            if (!(Objects.equals(validGiftCards.findById(giftCardId).owner(), user)))
                throw new RuntimeException(UserDoesNotOwnCardError);


        }


        private void validateTokenAndGiftCardInformation(Integer token, String giftCardId) {
            if (!isTokenValid(token))
                throw new RuntimeException(InvalidSessionError);

            if (!validGiftCards.existsById(giftCardId))
                throw new RuntimeException(InvalidGiftCardSelectedError);
        }
    }
