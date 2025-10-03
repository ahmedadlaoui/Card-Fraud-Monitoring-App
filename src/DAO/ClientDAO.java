package DAO;

import Entity.Client;
import JDBC.DBConnection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClientDAO {

    public void insertClient(Client client) {
        String insertClientQuery = "INSERT INTO client (id, name, email, phone) VALUES (?, ?, ?, ?)";
        PreparedStatement insertStatement = null;

        try {
            insertStatement = DBConnection.getInstance().getConnection().prepareStatement(insertClientQuery);
            insertStatement.setString(1, client.id().toString());
            insertStatement.setString(2, client.name());
            insertStatement.setString(3, client.email());
            insertStatement.setString(4, client.phone());

            if (insertStatement.executeUpdate() == 0) {
                throw new SQLException("Failed to save client");
            }
        } catch (SQLException databaseError) {
            if (databaseError.getMessage().contains("Duplicate entry")) {
                throw new RuntimeException("Email already exists");
            }
            throw new RuntimeException("Database error", databaseError);
        } finally {
            if (insertStatement != null) {
                try {
                    insertStatement.close();
                } catch (SQLException closeError) {
                    // Ignore close errors
                }
            }
        }
    }

}
