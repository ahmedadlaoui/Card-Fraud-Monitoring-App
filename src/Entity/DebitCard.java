package Entity;

import Entity.enums.CardStatus;

import java.time.LocalDate;
import java.util.UUID;

public final class DebitCard extends Card {
    private double dailyLimit;

    public DebitCard(UUID id, String cardNumber, LocalDate expiryDate,
                     CardStatus status, UUID clientId, double dailyLimit) {
        super(id, cardNumber, expiryDate, status, clientId);
        this.dailyLimit = dailyLimit;
    }

    public double getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(double dailyLimit) {
        this.dailyLimit = dailyLimit;
    }
}
