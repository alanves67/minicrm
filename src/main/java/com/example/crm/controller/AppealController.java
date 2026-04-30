package com.example.crm.controller;

import com.example.crm.dto.AppealDto;
import com.example.crm.service.AppealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appeals")
@RequiredArgsConstructor
public class AppealController {

    private final AppealService appealService;

    @GetMapping
    public Page<AppealDto> getAllAppeals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return appealService.getAppealsPage(page, size, sortBy, sortDir);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppealDto> getAppealById(@PathVariable Long id) {
        AppealDto appeal = appealService.getAppealById(id);
        return ResponseEntity.ok(appeal);
    }

    @GetMapping("/customer/{customerId}")
    public List<AppealDto> getAppealsByCustomerId(@PathVariable Long customerId) {
        return appealService.getAppealsByCustomerId(customerId);
    }

    @PostMapping
    public ResponseEntity<AppealDto> createAppeal(@Valid @RequestBody AppealDto appealDto) {
        AppealDto created = appealService.createAppeal(appealDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppealDto> updateAppeal(@PathVariable Long id,
                                                  @Valid @RequestBody AppealDto appealDto) {
        AppealDto updated = appealService.updateAppeal(id, appealDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppeal(@PathVariable Long id) {
        appealService.deleteAppeal(id);
        return ResponseEntity.noContent().build();
    }
}