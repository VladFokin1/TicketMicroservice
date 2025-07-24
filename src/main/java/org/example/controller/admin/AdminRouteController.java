package org.example.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.dto.ErrorResponse;
import org.example.dto.route.RouteCreateDto;
import org.example.dto.ticket.TicketUpdateDto;
import org.example.model.Route;
import org.example.model.Ticket;
import org.example.service.RouteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/routes")
@Tag(name = "Админ: Управление маршрутами", description = "Требуются права ADMIN")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRouteController {

    private final RouteService routeService;

    public AdminRouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @Operation(summary = "Создать маршрут")
    @ApiResponse(responseCode = "201", description = "Маршрут создан",
            content = @Content(schema = @Schema(implementation = Route.class)))
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    @PostMapping
    public ResponseEntity<Route> createRoute(@Valid @RequestBody RouteCreateDto dto) {
        Route route = routeService.createRoute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(route);
    }

    @Operation(summary = "Получить все маршруты")
    @ApiResponse(responseCode = "200", description = "Список маршрутов",
            content = @Content(schema = @Schema(implementation = List.class)))
    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        List<Route> routes = routeService.getAllRoutes();
        return ResponseEntity.ok(routes);
    }

    @Operation(summary = "Обновить маршрут")
    @ApiResponse(responseCode = "200", description = "Маршрут обновлен",
            content = @Content(schema = @Schema(implementation = Route.class)))
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Билет не найден")
    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    @PutMapping("/{id}")
    public ResponseEntity<Route> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody RouteCreateDto dto) {
        Route route = routeService.updateRoute(id, dto);
        return ResponseEntity.ok(route);
    }

    @Operation(summary = "Удалить билет")
    @ApiResponse(responseCode = "204", description = "Билет удален")
    @ApiResponse(responseCode = "404", description = "Билет не найден")
    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }
}
