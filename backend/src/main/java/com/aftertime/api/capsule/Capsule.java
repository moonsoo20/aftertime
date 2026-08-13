package com.aftertime.api.capsule;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import com.aftertime.api.user.AppUser;

@Entity
@Table(name = "capsules")
public class Capsule {
    @Id
    private UUID id;
    @Column(nullable = false, length = 80)
    private String title;
    @Column(nullable = false, length = 80)
    private String recipient;
    @Column(nullable = false, length = 4000)
    private String message;
    @Column(nullable = false)
    private Instant unlockAt;
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser owner;

    protected Capsule() {}

    public Capsule(String title, String recipient, String message, Instant unlockAt, AppUser owner) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.recipient = recipient;
        this.message = message;
        this.unlockAt = unlockAt;
        this.createdAt = Instant.now();
        this.owner = owner;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public Instant getUnlockAt() { return unlockAt; }
    public Instant getCreatedAt() { return createdAt; }
    public AppUser getOwner() { return owner; }
}
