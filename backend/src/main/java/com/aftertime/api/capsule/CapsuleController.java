package com.aftertime.api.capsule;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/capsules")
public class CapsuleController {
    private final CapsuleService service;
    public CapsuleController(CapsuleService service) { this.service = service; }

    @GetMapping
    public List<CapsuleDtos.Summary> list() { return service.findAll(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CapsuleDtos.Summary create(@Valid @RequestBody CapsuleDtos.CreateRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}/open")
    public CapsuleDtos.Detail open(@PathVariable UUID id) { return service.open(id); }
}
