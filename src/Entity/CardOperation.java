package Entity;

import Entity.enums.OperationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record CardOperation(
        UUID id,
        UUID cardId,
        LocalDateTime date,
        double amount,
        OperationType type,
        String location
) {
}
