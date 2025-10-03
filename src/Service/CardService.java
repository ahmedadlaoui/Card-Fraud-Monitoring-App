package Service;

import Entity.*;
import Entity.enums.CardStatus;
import DAO.CardDAO;
import JDBC.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

public class CardService {
    private Scanner scanner = new Scanner(System.in);
    private CardDAO cardDAO = new CardDAO();

    public void issueCard() {
        System.out.println("\n=== ISSUE NEW CARD ===");

        System.out.print("Enter client ID: ");
        String clientInput = scanner.nextLine().trim();

        UUID clientId = findClient(clientInput);
        if (clientId == null) {
            System.out.println("Client not found.");
            return;
        }

        System.out.println("1. Debit  2. Credit  3. Prepaid");
        System.out.print("Choose card type: ");
        int cardTypeChoice = getInt();

        Card newCard = null;
        switch (cardTypeChoice) {
            case 1:
                newCard = createDebit(clientId);
                break;
            case 2:
                newCard = createCredit(clientId);
                break;
            case 3:
                newCard = createPrepaid(clientId);
                break;
            default:
                newCard = null;
                break;
        }

        if (newCard != null) {
            cardDAO.insertCard(newCard);
            System.out.println("Card created: " + newCard.getCardNumber());
        } else {
            System.out.println("Invalid card type.");
        }
    }

    private UUID findClient(String clientInput) {
        String selectClientQuery = "SELECT id FROM client WHERE id = ? OR email = ?";
        try (PreparedStatement clientStatement = DBConnection.getInstance().getConnection()
                .prepareStatement(selectClientQuery)) {
            clientStatement.setString(1, clientInput);
            clientStatement.setString(2, clientInput);
            ResultSet clientResult = clientStatement.executeQuery();
            return clientResult.next() ? UUID.fromString(clientResult.getString("id")) : null;
        } catch (SQLException databaseError) {
            return null;
        }
    }

    private int getInt() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException parseError) {
            return 0;
        }
    }

    private double getDouble(String prompt, double defaultValue) {
        System.out.print(prompt + " (default " + defaultValue + "): ");
        String userInput = scanner.nextLine().trim();
        if (userInput.isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(userInput);
        } catch (NumberFormatException parseError) {
            return defaultValue;
        }
    }

    private String generateCardNumber() {
        Random randomGenerator = new Random();
        return String.format("%04d %04d %04d %04d",
                randomGenerator.nextInt(10000), randomGenerator.nextInt(10000),
                randomGenerator.nextInt(10000), randomGenerator.nextInt(10000));
    }

    private DebitCard createDebit(UUID clientId) {
        double dailyLimit = getDouble("Daily limit", 1000);
        UUID cardId = UUID.randomUUID();
        String cardNumber = generateCardNumber();
        LocalDate expirationDate = LocalDate.now().plusYears(3);
        return new DebitCard(cardId, cardNumber, expirationDate, CardStatus.ACTIVE, clientId, dailyLimit);
    }

    private CreditCard createCredit(UUID clientId) {
        double monthlyLimit = getDouble("Monthly limit", 5000);
        double interestRate = getDouble("Interest rate %", 15.5);
        UUID cardId = UUID.randomUUID();
        String cardNumber = generateCardNumber();
        LocalDate expirationDate = LocalDate.now().plusYears(3);
        return new CreditCard(cardId, cardNumber, expirationDate, CardStatus.ACTIVE, clientId, monthlyLimit,
                interestRate);
    }

    private PrepaidCard createPrepaid(UUID clientId) {
        double initialBalance = getDouble("Initial balance", 0);
        UUID cardId = UUID.randomUUID();
        String cardNumber = generateCardNumber();
        LocalDate expirationDate = LocalDate.now().plusYears(3);
        return new PrepaidCard(cardId, cardNumber, expirationDate, CardStatus.ACTIVE, clientId, initialBalance);
    }
}