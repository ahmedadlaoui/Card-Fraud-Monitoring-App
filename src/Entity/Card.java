package Entity;

import Entity.enums.CardStatus;

import java.time.LocalDate;
import java.util.UUID;

public sealed abstract class Card
        permits DebitCard, CreditCard, PrepaidCard {

    private final UUID id;
    private final String cardNumber;
    private final LocalDate expiryDate;
    private final UUID clientId;
    private CardStatus status;

    public Card(UUID id, String cardNumber, LocalDate expiryDate, CardStatus status, UUID clientId) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.status = status;
        this.clientId = clientId;
    }

    public UUID getId() {
        return id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public UUID getClientId() {
        return clientId;
    }
}
