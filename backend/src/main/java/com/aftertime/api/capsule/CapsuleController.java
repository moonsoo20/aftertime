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

    @GetMapping("/{id}")
    public CapsuleDtos.ManagementDetail detail(@PathVariable UUID id) { return service.findOne(id); }

    @PutMapping("/{id}")
    public CapsuleDtos.Summary update(@PathVariable UUID id, @Valid @RequestBody CapsuleDtos.UpdateRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { service.delete(id); }
}
