package com.aftertime.api.capsule;

import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.UUID;

public final class CapsuleDtos {
    private CapsuleDtos() {}

    public record CreateRequest(
        @NotBlank @Size(max = 80) String title,
        @NotBlank @Size(max = 80) String recipient,
        @NotBlank @Size(max = 4000) String message,
        @NotNull @Future Instant unlockAt
    ) {}

    public record Summary(UUID id, String title, String recipient, Instant unlockAt,
                          Instant createdAt, boolean unlocked) {
        static Summary from(Capsule capsule, Instant now) {
            return new Summary(capsule.getId(), capsule.getTitle(), capsule.getRecipient(),
                capsule.getUnlockAt(), capsule.getCreatedAt(), !now.isBefore(capsule.getUnlockAt()));
        }
    }

    public record Detail(UUID id, String title, String recipient, String message,
                         Instant unlockAt, Instant createdAt) {
        static Detail from(Capsule capsule) {
            return new Detail(capsule.getId(), capsule.getTitle(), capsule.getRecipient(),
                capsule.getMessage(), capsule.getUnlockAt(), capsule.getCreatedAt());
        }
    }
}

