package Entity;

import Entity.enums.CardStatus;

import java.time.LocalDate;
import java.util.UUID;

public final class PrepaidCard extends Card {
    private double balance;

    public PrepaidCard(UUID id, String cardNumber, LocalDate expiryDate,
                       CardStatus status, UUID clientId, double balance) {
        super(id, cardNumber, expiryDate, status, clientId);
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
