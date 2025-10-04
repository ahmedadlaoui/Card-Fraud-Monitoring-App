package Service;

import Entity.FraudAlert;
import DAO.AlertDAO;
import java.util.List;

public class FraudService {
    private AlertDAO alertDAO = new AlertDAO();

    public void displayFraudAnalysis() {
        System.out.println("\n=== FRAUD ANALYSIS ===");

        List<FraudAlert> fraudAlerts = alertDAO.getAllFraudAlerts();

        if (fraudAlerts.isEmpty()) {
            System.out.println("No fraud alerts found.");
            return;
        }

        System.out.println("\nFraud Alerts Found: " + fraudAlerts.size());

        for (int i = 0; i < fraudAlerts.size(); i++) {
            FraudAlert alert = fraudAlerts.get(i);
            System.out.println("\n" + (i + 1) + ". Card: " + alert.cardNumber());
            System.out.println("   Level: " + alert.level());
            System.out.println("   Details: " + alert.description());
        }
    }
}