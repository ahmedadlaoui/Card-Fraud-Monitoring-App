package Service;

import Entity.*;
import Entity.enums.OperationType;
import Entity.enums.FraudLevel;
import DAO.OperationDAO;
import DAO.AlertDAO;
import JDBC.DBConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.UUID;

public class OperationService {
    private Scanner scanner = new Scanner(System.in);
    private OperationDAO operationDAO = new OperationDAO();
    private AlertDAO alertDAO = new AlertDAO();

    public void performOperation() {
        System.out.println("\n=== PERFORM OPERATION ===");

        System.out.print("Enter card number: ");
        String cardNumber = scanner.nextLine().trim();

        Card userCard = findCard(cardNumber);
        if (userCard == null) {
            System.out.println("Card not found.");
            return;
        }

        System.out.println("1. Purchase  2. Withdrawal  3. Online Payment");
        System.out.print("Choose operation: ");
        int operationChoice = getInt();

        OperationType operationType = null;
        switch (operationChoice) {
            case 1:
                operationType = OperationType.PURCHASE;
                break;
            case 2:
                operationType = OperationType.WITHDRAWAL;
                break;
            case 3:
                operationType = OperationType.ONLINE_PAYMENT;
                break;
            default:
                operationType = null;
                break;
        }

        if (operationType == null) {
            System.out.println("Invalid operation type.");
            return;
        }

        System.out.print("Enter amount: ");
        double operationAmount = getDouble();
        if (operationAmount <= 0) {
            System.out.println("Amount must be positive.");
            return;
        }

        System.out.print("Enter location: ");
        String operationLocation = scanner.nextLine().trim();
        if (operationLocation.isEmpty()) {
            operationLocation = "Unknown";
        }

        String failureReason = processOperation(userCard, operationType, operationAmount);
        if (failureReason == null) {
            CardOperation newOperation = new CardOperation(
                    UUID.randomUUID(),
                    userCard.getId(),
                    LocalDateTime.now(),
                    operationAmount,
                    operationType,
                    operationLocation);

            operationDAO.insertOperation(newOperation);
            updateCardBalanceAfterOperation(userCard, operationAmount);
            System.out.println("Operation successful: " + operationType + " - $" + operationAmount);
        } else {
            // Register fraud alert for failed operation
            FraudAlert fraudAlert = new FraudAlert(
                    UUID.randomUUID(),
                    userCard.getId(),
                    "Failed " + operationType + " operation: $" + operationAmount + " - " + failureReason,
                    FraudLevel.WARNING,
                    userCard.getCardNumber());
            alertDAO.insertFraudAlert(fraudAlert);
            System.out.println("Operation failed: " + failureReason);
        }
    }

    public void getCardOperations() {
        System.out.println("\n=== VIEW CARD HISTORY ===");

        System.out.print("Enter card number: ");
        String cardNumber = scanner.nextLine().trim();

        Card userCard = findCard(cardNumber);
        if (userCard == null) {
            System.out.println("Card not found.");
            return;
        }

        String historyQuery = "SELECT operationDate, operationType, amount, location FROM cardoperation WHERE cardId = ? ORDER BY operationDate DESC";
        PreparedStatement historyStatement = null;
        ResultSet historyResult = null;

        try {
            historyStatement = DBConnection.getInstance().getConnection().prepareStatement(historyQuery);
            historyStatement.setString(1, userCard.getId().toString());
            historyResult = historyStatement.executeQuery();

            boolean hasOperations = false;
            int operationCount = 0;

            // First, collect all operations
            java.util.List<String> operations = new java.util.ArrayList<>();
            while (historyResult.next()) {
                hasOperations = true;
                operationCount++;

                String date = historyResult.getTimestamp("operationDate").toString().substring(0, 16);
                String type = historyResult.getString("operationType");
                double amount = historyResult.getDouble("amount");
                String location = historyResult.getString("location");

                operations.add(operationCount + ". " + type + " - $" + amount + "\n" +
                        "   Date: " + date + "\n" +
                        "   Location: " + location);
            }

            if (!hasOperations) {
                System.out.println("No operations found for this card.");
            } else {
                System.out.println("\nCard: " + cardNumber);
                System.out.println("Operations Found: " + operationCount);

                for (String operation : operations) {
                    System.out.println("\n" + operation);
                }
            }

        } catch (SQLException databaseError) {
            System.out.println("Error retrieving card history: " + databaseError.getMessage());
        } finally {
            try {
                if (historyResult != null) {
                    historyResult.close();
                }
                if (historyStatement != null) {
                    historyStatement.close();
                }
            } catch (SQLException closeError) {
                // Ignore close errors
            }
        }
    }

    private Card findCard(String cardNumber) {
        String findCardQuery = "SELECT * FROM card WHERE number = ?";
        PreparedStatement cardStatement = null;
        ResultSet cardResult = null;

        try {
            cardStatement = DBConnection.getInstance().getConnection().prepareStatement(findCardQuery);
            cardStatement.setString(1, cardNumber);
            cardResult = cardStatement.executeQuery();

            if (cardResult.next()) {
                UUID cardId = UUID.fromString(cardResult.getString("id"));
                UUID clientId = UUID.fromString(cardResult.getString("clientId"));
                String cardType = cardResult.getString("cardType");

                if (cardType.equals("DEBIT")) {
                    return createDebitFromResult(cardResult, cardId, clientId);
                } else if (cardType.equals("CREDIT")) {
                    return createCreditFromResult(cardResult, cardId, clientId);
                } else if (cardType.equals("PREPAID")) {
                    return createPrepaidFromResult(cardResult, cardId, clientId);
                } else {
                    return null;
                }
            }
        } catch (SQLException databaseError) {
            System.out.println("Database error: " + databaseError.getMessage());
        } finally {
            try {
                if (cardResult != null) {
                    cardResult.close();
                }
                if (cardStatement != null) {
                    cardStatement.close();
                }
            } catch (SQLException closeError) {
                // Ignore close errors
            }
        }
        return null;
    }

    private DebitCard createDebitFromResult(ResultSet cardResult, UUID cardId, UUID clientId) throws SQLException {
        return new DebitCard(
                cardId,
                cardResult.getString("number"),
                cardResult.getDate("expirationDate").toLocalDate(),
                Entity.enums.CardStatus.valueOf(cardResult.getString("status")),
                clientId,
                cardResult.getDouble("dailyLimit"));
    }

    private CreditCard createCreditFromResult(ResultSet cardResult, UUID cardId, UUID clientId) throws SQLException {
        return new CreditCard(
                cardId,
                cardResult.getString("number"),
                cardResult.getDate("expirationDate").toLocalDate(),
                Entity.enums.CardStatus.valueOf(cardResult.getString("status")),
                clientId,
                cardResult.getDouble("monthlyLimit"),
                cardResult.getDouble("interestRate"));
    }

    private PrepaidCard createPrepaidFromResult(ResultSet cardResult, UUID cardId, UUID clientId) throws SQLException {
        PrepaidCard prepaidCard = new PrepaidCard(
                cardId,
                cardResult.getString("number"),
                cardResult.getDate("expirationDate").toLocalDate(),
                Entity.enums.CardStatus.valueOf(cardResult.getString("status")),
                clientId,
                cardResult.getDouble("balance"));
        return prepaidCard;
    }

    private String processOperation(Card userCard, OperationType operationType, double operationAmount) {
        if (userCard instanceof DebitCard) {
            DebitCard debitCard = (DebitCard) userCard;
            return processDebitOperation(debitCard, operationType, operationAmount);
        } else if (userCard instanceof CreditCard) {
            CreditCard creditCard = (CreditCard) userCard;
            return processCreditOperation(creditCard, operationType, operationAmount);
        } else if (userCard instanceof PrepaidCard) {
            PrepaidCard prepaidCard = (PrepaidCard) userCard;
            return processPrepaidOperation(prepaidCard, operationType, operationAmount);
        }
        return "Unknown card type";
    }

    private String processDebitOperation(DebitCard debitCard, OperationType operationType, double operationAmount) {
        double currentBalance = getCurrentBalance(debitCard.getId());

        if (operationAmount > debitCard.getDailyLimit()) {
            return "Amount exceeds daily limit of $" + debitCard.getDailyLimit();
        }

        if (operationAmount > currentBalance) {
            return "Insufficient balance. Current balance: $" + currentBalance;
        }

        return null;
    }

    private String processCreditOperation(CreditCard creditCard, OperationType operationType, double operationAmount) {
        double currentBalance = getCurrentBalance(creditCard.getId());
        double availableCredit = creditCard.getMonthlyLimit() + currentBalance;

        if (operationAmount > availableCredit) {
            return "Amount exceeds available credit of $" + availableCredit;
        }

        return null;
    }

    private String processPrepaidOperation(PrepaidCard prepaidCard, OperationType operationType,
            double operationAmount) {
        double currentBalance = prepaidCard.getBalance();

        if (operationAmount > currentBalance) {
            return "Insufficient balance. Current balance: $" + currentBalance;
        }

        prepaidCard.setBalance(currentBalance - operationAmount);
        return null;
    }

    private double getCurrentBalance(UUID cardId) {
        String balanceQuery = "SELECT balance FROM card WHERE id = ?";
        PreparedStatement balanceStatement = null;
        ResultSet balanceResult = null;

        try {
            balanceStatement = DBConnection.getInstance().getConnection().prepareStatement(balanceQuery);
            balanceStatement.setString(1, cardId.toString());
            balanceResult = balanceStatement.executeQuery();

            if (balanceResult.next()) {
                return balanceResult.getDouble("balance");
            } else {
                return 0.0;
            }
        } catch (SQLException databaseError) {
            return 0.0;
        } finally {
            try {
                if (balanceResult != null) {
                    balanceResult.close();
                }
                if (balanceStatement != null) {
                    balanceStatement.close();
                }
            } catch (SQLException closeError) {
                // Ignore close errors
            }
        }
    }

    private int getInt() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException parseError) {
            return 0;
        }
    }

    private double getDouble() {
        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException parseError) {
            return 0.0;
        }
    }

    private void updateCardBalanceAfterOperation(Card userCard, double operationAmount) {
        String updateBalanceQuery = "UPDATE card SET balance = balance - ? WHERE id = ?";
        PreparedStatement balanceStatement = null;

        try {
            balanceStatement = DBConnection.getInstance().getConnection().prepareStatement(updateBalanceQuery);
            balanceStatement.setDouble(1, operationAmount);
            balanceStatement.setString(2, userCard.getId().toString());
            balanceStatement.executeUpdate();
        } catch (SQLException databaseError) {
            System.out.println("Error updating balance: " + databaseError.getMessage());
        } finally {
            if (balanceStatement != null) {
                try {
                    balanceStatement.close();
                } catch (SQLException closeError) {
                    // Ignore close errors
                }
            }
        }
    }
}