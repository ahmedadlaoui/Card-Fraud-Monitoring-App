package DAO;

import Entity.FraudAlert;
import JDBC.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlertDAO {

    public void insertFraudAlert(FraudAlert fraudAlert) {
        String insertQuery = "INSERT INTO fraudalert (id, cardId, description, level) VALUES (?, ?, ?, ?)";
        PreparedStatement insertStatement = null;

        try {
            insertStatement = DBConnection.getInstance().getConnection().prepareStatement(insertQuery);
            insertStatement.setString(1, fraudAlert.id().toString());
            insertStatement.setString(2, fraudAlert.cardId().toString());
            insertStatement.setString(3, fraudAlert.description());
            insertStatement.setString(4, fraudAlert.level().toString());
            insertStatement.executeUpdate();
        } catch (SQLException databaseError) {
            System.out.println("Error inserting fraud alert: " + databaseError.getMessage());
        } finally {
            try {
                if (insertStatement != null) {
                    insertStatement.close();
                }
            } catch (SQLException closeError) {
                // Ignore close errors
            }
        }
    }

    public List<FraudAlert> getAllFraudAlerts() {
        List<FraudAlert> fraudAlerts = new ArrayList<>();
        String selectQuery = "SELECT f.id, f.cardId, f.description, f.level, c.number " +
                "FROM fraudalert f " +
                "JOIN card c ON f.cardId = c.id " +
                "ORDER BY f.id DESC";
        PreparedStatement selectStatement = null;
        ResultSet selectResult = null;

        try {
            selectStatement = DBConnection.getInstance().getConnection().prepareStatement(selectQuery);
            selectResult = selectStatement.executeQuery();

            while (selectResult.next()) {
                FraudAlert fraudAlert = new FraudAlert(
                        java.util.UUID.fromString(selectResult.getString("id")),
                        java.util.UUID.fromString(selectResult.getString("cardId")),
                        selectResult.getString("description"),
                        Entity.enums.FraudLevel.valueOf(selectResult.getString("level")),
                        selectResult.getString("number"));
                fraudAlerts.add(fraudAlert);
            }
        } catch (SQLException databaseError) {
            System.out.println("Error retrieving fraud alerts: " + databaseError.getMessage());
        } finally {
            try {
                if (selectResult != null) {
                    selectResult.close();
                }
                if (selectStatement != null) {
                    selectStatement.close();
                }
            } catch (SQLException closeError) {
                // Ignore close errors
            }
        }

        return fraudAlerts;
    }
}
