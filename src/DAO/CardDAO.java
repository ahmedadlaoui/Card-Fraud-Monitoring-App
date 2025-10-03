package DAO;

import Entity.*;
import JDBC.DBConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CardDAO {

    public void insertCard(Card card) {
        String insertCardQuery = "INSERT INTO card (id, number, expirationDate, status, cardType, dailyLimit, monthlyLimit, interestRate, balance, clientId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement cardStatement = DBConnection.getInstance().getConnection()
                .prepareStatement(insertCardQuery)) {
            cardStatement.setString(1, card.getId().toString());
            cardStatement.setString(2, card.getCardNumber());
            cardStatement.setDate(3, java.sql.Date.valueOf(card.getExpiryDate()));
            cardStatement.setString(4, card.getStatus().name());

            if (card instanceof DebitCard debitCard) {
                cardStatement.setString(5, "DEBIT");
                cardStatement.setDouble(6, debitCard.getDailyLimit());
                cardStatement.setNull(7, java.sql.Types.DECIMAL);
                cardStatement.setNull(8, java.sql.Types.DECIMAL);
                cardStatement.setDouble(9, 0.0);
            } else if (card instanceof CreditCard creditCard) {
                cardStatement.setString(5, "CREDIT");
                cardStatement.setNull(6, java.sql.Types.DECIMAL);
                cardStatement.setDouble(7, creditCard.getMonthlyLimit());
                cardStatement.setDouble(8, creditCard.getInterestRate());
                cardStatement.setDouble(9, 0.0);
            } else if (card instanceof PrepaidCard prepaidCard) {
                cardStatement.setString(5, "PREPAID");
                cardStatement.setNull(6, java.sql.Types.DECIMAL);
                cardStatement.setNull(7, java.sql.Types.DECIMAL);
                cardStatement.setNull(8, java.sql.Types.DECIMAL);
                cardStatement.setDouble(9, prepaidCard.getBalance());
            }

            cardStatement.setString(10, card.getClientId().toString());

            if (cardStatement.executeUpdate() == 0) {
                throw new SQLException("Failed to insert card");
            }
        } catch (SQLException databaseError) {
            if (databaseError.getMessage().contains("Duplicate entry")) {
                throw new RuntimeException("Card number already exists");
            }
            throw new RuntimeException("Database error", databaseError);
        }
    }

    public void updateCardBalance(Card card) {
        String updateBalanceQuery = "UPDATE card SET balance = ? WHERE id = ?";

        try (PreparedStatement balanceStatement = DBConnection.getInstance().getConnection()
                .prepareStatement(updateBalanceQuery)) {
            if (card instanceof PrepaidCard prepaidCard) {
                balanceStatement.setDouble(1, prepaidCard.getBalance());
            } else {
                // For debit and credit cards, subtract the operation amount from current
                // balance
                double currentBalance = getCurrentBalance(card.getId());
                balanceStatement.setDouble(1, currentBalance);
            }

            balanceStatement.setString(2, card.getId().toString());
            balanceStatement.executeUpdate();
        } catch (SQLException databaseError) {
            throw new RuntimeException("Database error", databaseError);
        }
    }

    private double getCurrentBalance(UUID cardId) {
        String balanceQuery = "SELECT balance FROM card WHERE id = ?";
        try (PreparedStatement balanceStatement = DBConnection.getInstance().getConnection()
                .prepareStatement(balanceQuery)) {
            balanceStatement.setString(1, cardId.toString());
            ResultSet balanceResult = balanceStatement.executeQuery();
            return balanceResult.next() ? balanceResult.getDouble("balance") : 0.0;
        } catch (SQLException databaseError) {
            return 0.0;
        }
    }
}
