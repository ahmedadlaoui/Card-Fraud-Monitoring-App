package Entity;

import java.util.UUID;

public record Client(
        UUID id,
        String name,
        String email,
        String phone
) {
}
