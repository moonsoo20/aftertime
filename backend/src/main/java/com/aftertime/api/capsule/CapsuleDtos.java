package com.aftertime.api.capsule;

import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.UUID;

public final class CapsuleDtos {
    private CapsuleDtos() {}

    public record CreateRequest(@NotBlank @Size(max=80) String title,
        @NotBlank @Size(max=80) String recipient, @NotBlank @Size(max=4000) String message,
        @NotNull @Future Instant unlockAt) {}

    public record UpdateRequest(@NotBlank @Size(max=80) String title,
        @NotBlank @Size(max=80) String recipient, @NotBlank @Size(max=4000) String message,
        @NotNull @Future Instant unlockAt) {}

    public record Summary(UUID id, String title, String recipient, Instant unlockAt,
                          Instant createdAt, boolean unlocked) {
        static Summary from(Capsule c, Instant now) { return new Summary(c.getId(),c.getTitle(),c.getRecipient(),c.getUnlockAt(),c.getCreatedAt(),!now.isBefore(c.getUnlockAt())); }
    }

    public record Detail(UUID id,String title,String recipient,String message,Instant unlockAt,Instant createdAt) {
        static Detail from(Capsule c) { return new Detail(c.getId(),c.getTitle(),c.getRecipient(),c.getMessage(),c.getUnlockAt(),c.getCreatedAt()); }
    }

    public record ManagementDetail(UUID id,String title,String recipient,String message,
        Instant unlockAt,Instant createdAt,boolean unlocked,boolean editable,Instant editableUntil) {
        static ManagementDetail from(Capsule c,Instant now,Instant until) {
            boolean editable=now.isBefore(until)&&now.isBefore(c.getUnlockAt());
            return new ManagementDetail(c.getId(),c.getTitle(),c.getRecipient(),editable?c.getMessage():null,c.getUnlockAt(),c.getCreatedAt(),!now.isBefore(c.getUnlockAt()),editable,until);
        }
    }
}
