package com.aftertime.api.capsule;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import com.aftertime.api.user.AppUser;

public interface CapsuleRepository extends JpaRepository<Capsule, UUID> {
    List<Capsule> findAllByOwnerOrderByCreatedAtDesc(AppUser owner);
    java.util.Optional<Capsule> findByIdAndOwner(UUID id, AppUser owner);
}
