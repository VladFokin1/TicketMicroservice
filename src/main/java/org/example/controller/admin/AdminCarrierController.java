package org.example.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.dto.ErrorResponse;
import org.example.dto.carrier.CarrierCreateDto;
import org.example.dto.ticket.TicketUpdateDto;
import org.example.model.Carrier;
import org.example.model.Ticket;
import org.example.service.CarrierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/carriers")
@Tag(name = "Админ: Управление перевозчиками", description = "Требуются права ADMIN")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCarrierController {

    private final CarrierService carrierService;

    public AdminCarrierController(CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    @Operation(summary = "Добавить перевозчика")
    @ApiResponse(responseCode = "201", description = "Перевозчик создан",
            content = @Content(schema = @Schema(implementation = Carrier.class)))
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    @PostMapping
    public ResponseEntity<Carrier> createCarrier(@Valid @RequestBody CarrierCreateDto dto) {
        Carrier carrier = carrierService.createCarrier(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(carrier);
    }

    @Operation(summary = "Получить всех перевозчиков")
    @ApiResponse(responseCode = "200", description = "Список перевозчиков",
            content = @Content(schema = @Schema(implementation = List.class)))
    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    @GetMapping
    public ResponseEntity<List<Carrier>> getAllCarriers() {
        List<Carrier> carriers = carrierService.getAllCarriers();
        return ResponseEntity.ok(carriers);
    }

    @Operation(summary = "Обновить перевозчика")
    @ApiResponse(responseCode = "200", description = "Перевозчик обновлен",
            content = @Content(schema = @Schema(implementation = Carrier.class)))
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Перевозчик не найден")
    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    @PutMapping("/{id}")
    public ResponseEntity<Carrier> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody CarrierCreateDto dto) {
        Carrier carrier = carrierService.updateCarrier(id, dto);
        return ResponseEntity.ok(carrier);
    }

    @Operation(summary = "Удаление перевозчика")
    @ApiResponse(responseCode = "204", description = "Перевозчик удален")
    @ApiResponse(responseCode = "404", description = "Перевозчик не найден")
    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarrier(@PathVariable Long id) {
        carrierService.deleteCarrier(id);
        return ResponseEntity.noContent().build();
    }

}
