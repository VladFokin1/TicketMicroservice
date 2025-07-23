package org.example.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.example.dto.ErrorResponse;
import org.example.dto.PurchaseTicketDto;
import org.example.dto.TicketFilterDto;
import org.example.model.Ticket;
import org.example.service.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Билеты", description = "Управление билетами")
@Validated
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Operation(summary = "Получить доступные билеты", description = "С фильтрацией и пагинацией")
    @ApiResponse(responseCode = "200", description = "Успешный запрос",
            content = @Content(schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/available")
    public ResponseEntity<Page<Ticket>> getAvailableTickets(
            @Valid TicketFilterDto filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Передаем page и size как отдельные параметры
        Page<Ticket> tickets = ticketService.getAvailableTickets(filter, page, size);
        return ResponseEntity.ok(tickets);
    }

    @Operation(summary = "Купить билет")
    @ApiResponse(responseCode = "200", description = "Билет успешно куплен")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Билет не найден")
    @ApiResponse(responseCode = "409", description = "Билет уже продан")
    @PostMapping("/purchase")
    public ResponseEntity<Void> purchaseTicket(@Valid @RequestBody PurchaseTicketDto purchaseDto) {
        ticketService.purchaseTicket(purchaseDto.getTicketId(), purchaseDto.getUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить купленные билеты пользователя")
    @ApiResponse(responseCode = "200", description = "Успешный запрос",
            content = @Content(schema = @Schema(implementation = Ticket[].class)))
    @ApiResponse(responseCode = "400", description = "Некорректный ID пользователя")
    @GetMapping("/purchased/{userId}")
    public ResponseEntity<List<Ticket>> getPurchasedTickets(
            @PathVariable @Min(1) Long userId) {

        List<Ticket> tickets = ticketService.getPurchasedTickets(userId);
        return ResponseEntity.ok(tickets);
    }
}
