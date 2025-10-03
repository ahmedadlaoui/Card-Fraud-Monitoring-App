package Service;

import Entity.Client;
import DAO.ClientDAO;
import java.util.Scanner;
import java.util.UUID;

public class ClientService {
    private Scanner scanner = new Scanner(System.in);
    private ClientDAO clientDAO = new ClientDAO();

    public void addUser() {
        System.out.println("\n=== CREATE CLIENT ===");

        System.out.print("Name: ");
        String clientName = scanner.nextLine().trim();
        if (clientName.isEmpty()) {
            System.out.println("Name required.");
            return;
        }

        System.out.print("Email: ");
        String clientEmail = scanner.nextLine().trim();
        if (clientEmail.isEmpty() || !clientEmail.contains("@")) {
            System.out.println("Valid email required.");
            return;
        }

        System.out.print("Phone (optional): ");
        String clientPhone = scanner.nextLine().trim();
        if (clientPhone.isEmpty())
            clientPhone = null;

        UUID clientId = UUID.randomUUID();
        Client newClient = new Client(clientId, clientName, clientEmail, clientPhone);

        try {
            clientDAO.insertClient(newClient);
            System.out.println("Client created: " + newClient.id());
        } catch (Exception databaseError) {
            System.out.println("Error: " + databaseError.getMessage());
        }
    }
}
