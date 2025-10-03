package DAO;

import Entity.CardOperation;
import JDBC.DBConnection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class OperationDAO {

    public void insertOperation(CardOperation operation) {
        String insertOperationQuery = "INSERT INTO cardoperation (id, operationDate, amount, operationType, location, cardId) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement operationStatement = null;

        try {
            operationStatement = DBConnection.getInstance().getConnection().prepareStatement(insertOperationQuery);
            operationStatement.setString(1, operation.id().toString());
            operationStatement.setTimestamp(2, Timestamp.valueOf(operation.date()));
            operationStatement.setDouble(3, operation.amount());
            operationStatement.setString(4, operation.type().name());
            operationStatement.setString(5, operation.location());
            operationStatement.setString(6, operation.cardId().toString());

            if (operationStatement.executeUpdate() == 0) {
                throw new SQLException("Failed to insert operation");
            }
        } catch (SQLException databaseError) {
            throw new RuntimeException("Database error", databaseError);
        } finally {
            if (operationStatement != null) {
                try {
                    operationStatement.close();
                } catch (SQLException closeError) {
                    // Ignore close errors
                }
            }
        }
    }
}
