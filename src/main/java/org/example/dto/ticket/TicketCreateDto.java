package org.example.dto.ticket;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TicketCreateDto {
    @NotNull
    private Integer routeId;

    @NotNull @Future
    private LocalDateTime dateTime;

    @NotBlank
    private String seatNumber;

    @NotNull @Positive
    private BigDecimal price;
}
