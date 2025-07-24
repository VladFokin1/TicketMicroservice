package org.example.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.dto.ticket.TicketCreateDto;
import org.example.dto.ErrorResponse;
import org.example.dto.ticket.TicketUpdateDto;
import org.example.model.Ticket;
import org.example.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tickets")
@Tag(name = "Админ: Управление билетами", description = "Требуются права ADMIN")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTicketController {

    private final TicketService ticketService;

    public AdminTicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Operation(summary = "Создать билет")
    @ApiResponse(responseCode = "201", description = "Билет создан",
            content = @Content(schema = @Schema(implementation = Ticket.class)))
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    @PostMapping
    public ResponseEntity<Ticket> createTicket(@Valid @RequestBody TicketCreateDto dto) {
        Ticket ticket = ticketService.createTicket(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }

    @Operation(summary = "Обновить билет")
    @ApiResponse(responseCode = "200", description = "Билет обновлен",
            content = @Content(schema = @Schema(implementation = Ticket.class)))
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Билет не найден")
    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody TicketUpdateDto dto) {
        Ticket ticket = ticketService.updateTicket(id, dto);
        return ResponseEntity.ok(ticket);
    }

    @Operation(summary = "Удалить билет")
    @ApiResponse(responseCode = "204", description = "Билет удален")
    @ApiResponse(responseCode = "404", description = "Билет не найден")
    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить все билеты")
    @ApiResponse(responseCode = "200", description = "Список билетов",
            content = @Content(schema = @Schema(implementation = List.class)))
    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }
}
