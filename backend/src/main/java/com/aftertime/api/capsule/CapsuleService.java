package com.aftertime.api.capsule;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.aftertime.api.user.*;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class CapsuleService {
    private final CapsuleRepository repository;
    private final AppUserRepository users;
    private final Clock clock = Clock.systemUTC();

    public CapsuleService(CapsuleRepository repository, AppUserRepository users) { this.repository = repository; this.users = users; }
    private AppUser currentUser() {
        return users.findByEmailIgnoreCase(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();
    }

    public CapsuleDtos.Summary create(CapsuleDtos.CreateRequest request) {
        Capsule capsule = repository.save(new Capsule(request.title().trim(), request.recipient().trim(),
            request.message().trim(), request.unlockAt(), currentUser()));
        return CapsuleDtos.Summary.from(capsule, clock.instant());
    }

    public List<CapsuleDtos.Summary> findAll() {
        Instant now = clock.instant();
        return repository.findAllByOwnerOrderByCreatedAtDesc(currentUser()).stream()
            .map(c -> CapsuleDtos.Summary.from(c, now)).toList();
    }

    public CapsuleDtos.Detail open(UUID id) {
        Capsule capsule = repository.findByIdAndOwner(id, currentUser())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "캡슐을 찾을 수 없습니다."));
        if (clock.instant().isBefore(capsule.getUnlockAt())) {
            throw new ResponseStatusException(HttpStatus.LOCKED, "아직 열 수 없는 캡슐입니다.");
        }
        return CapsuleDtos.Detail.from(capsule);
    }
}
