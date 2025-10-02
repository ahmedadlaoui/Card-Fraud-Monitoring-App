package Entity;

import Entity.enums.CardStatus;

import java.time.LocalDate;
import java.util.UUID;

public final class CreditCard extends Card {
    private double monthlyLimit;
    private double interestRate;

    public CreditCard(UUID id, String cardNumber, LocalDate expiryDate,
                      CardStatus status, UUID clientId,
                      double monthlyLimit, double interestRate) {
        super(id, cardNumber, expiryDate, status, clientId);
        this.monthlyLimit = monthlyLimit;
        this.interestRate = interestRate;
    }

    public double getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(double monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
}
