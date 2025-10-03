package ConsoleUI;

import java.util.Scanner;
import Service.ClientService;
import Service.CardService;
import Service.OperationService;

public class UserMenu {

    private ClientService clientService;
    private CardService cardService;
    private OperationService operationService;
    private Scanner scanner;

    public UserMenu() {
        this.clientService = new ClientService();
        this.cardService = new CardService();
        this.operationService = new OperationService();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("        CARD FRAUD MONITORING SYSTEM");
            System.out.println("=".repeat(50));
            System.out.println("1. Create a client");
            System.out.println("2. Issue a card (debit, credit, prepaid)");
            System.out.println("3. Perform an operation (purchase, withdrawal, online payment)");
            System.out.println("4. View card history");
            System.out.println("5. Run fraud analysis");
            System.out.println("6. Block/suspend a card");
            System.out.println("0. Exit");
            System.out.println("=".repeat(50));
            System.out.print("Please select an option (0-6): ");

            int choice = getChoice();

            switch (choice) {
                case 1:
                    this.clientService.addUser();
                    break;
                case 2:
                    this.cardService.issueCard();
                    break;
                case 3:
                    this.operationService.performOperation();
                    break;
                case 4:
                    this.operationService.getCardOperations();
                    break;
                case 5:

                    break;
                case 6:

                    break;
                case 0:
                    System.out.println("Thank you for using Card Fraud Monitoring System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option! Please select a number between 0 and 6.");
            }
        }

        scanner.close();
    }

    private int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
