package com.tecnocampus.examsimulation.api;

import com.tecnocampus.examsimulation.api.application.KingdomService;
import com.tecnocampus.examsimulation.api.domain.Kingdom;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/kingdoms")
@Tag(name = "Kingdom Management", description = "Endpoints for managing medieval kingdoms")
public class KingdomController {
    private final KingdomService kingdomService;

    public KingdomController(KingdomService kingdomService) {
        this.kingdomService = kingdomService;
    }

    @Operation(summary = "Create a new kingdom", description = "Creates a kingdom with initial resources")
    @ApiResponse(responseCode = "201", description = "Kingdom successfully created")
    @PostMapping
    public ResponseEntity<Kingdom> createKingdom(@Valid @RequestBody Kingdom kingdom) {
        Kingdom createdKingdom = kingdomService.createKingdom(kingdom);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdKingdom);
    }

    @Operation(summary = "Start daily production", description = "Process daily production cycle")
    @ApiResponse(responseCode = "200", description = "Production completed successfully")
    @ApiResponse(responseCode = "404", description = "Kingdom not found")
    @ApiResponse(responseCode = "406", description = "Not acceptable (no citizens left)")
    @PostMapping("/{id}")
    public ResponseEntity<Kingdom> startDailyProduction(
            @Parameter(description = "ID of the kingdom to process") @PathVariable String id) {
        try {
            Kingdom kingdom = kingdomService.startDailyProduction(id);
            return ResponseEntity.ok(kingdom);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
    }

    @Operation(summary = "Invest resources", description = "Convert gold to food or citizens")
    @ApiResponse(responseCode = "200", description = "Investment successful")
    @ApiResponse(responseCode = "400", description = "Invalid investment type")
    @ApiResponse(responseCode = "404", description = "Kingdom not found")
    @ApiResponse(responseCode = "406", description = "Not enough gold")
    @PostMapping("/{id}/invest")
    public ResponseEntity<Kingdom> invest(
            @Parameter(description = "ID of the kingdom") @PathVariable String id,
            @Parameter(description = "Type of investment (food or citizens)", example = "food")
            @RequestParam String type,
            @Valid @RequestBody Kingdom.InvestRequest investRequest) {
        try {
            Kingdom kingdom = kingdomService.invest(id, type, investRequest.getGold());
            return ResponseEntity.ok(kingdom);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
    }

    @Operation(summary = "Get kingdom status", description = "Retrieve current kingdom status")
    @ApiResponse(responseCode = "200", description = "Kingdom found")
    @ApiResponse(responseCode = "404", description = "Kingdom not found")
    @GetMapping("/{id}")
    public ResponseEntity<Kingdom> getKingdom(
            @Parameter(description = "ID of the kingdom") @PathVariable String id) {
        try {
            Kingdom kingdom = kingdomService.getKingdom(id);
            return ResponseEntity.ok(kingdom);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get richest kingdom", description = "Retrieve the kingdom with most gold")
    @ApiResponse(responseCode = "200", description = "Kingdom found")
    @ApiResponse(responseCode = "404", description = "No kingdoms exist")
    @GetMapping("/richest")
    public ResponseEntity<Kingdom> getRichestKingdom() {
        try {
            Kingdom kingdom = kingdomService.getRichestKingdom();
            return ResponseEntity.ok(kingdom);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Attack another kingdom", description = "Initiate attack between kingdoms")
    @ApiResponse(responseCode = "200", description = "Attack completed")
    @ApiResponse(responseCode = "404", description = "Kingdom not found")
    @PostMapping("/{id}/attack/{targetId}")
    public ResponseEntity<Kingdom> attack(
            @Parameter(description = "ID of attacking kingdom") @PathVariable String id,
            @Parameter(description = "ID of target kingdom") @PathVariable String targetId) {
        try {
            Kingdom kingdom = kingdomService.attack(id, targetId);
            return ResponseEntity.ok(kingdom);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}