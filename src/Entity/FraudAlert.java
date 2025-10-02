package Entity;

import Entity.enums.FraudLevel;

import java.util.UUID;

public record FraudAlert(
        UUID id,
        UUID cardId,
        String description,
        FraudLevel level
) {
}
