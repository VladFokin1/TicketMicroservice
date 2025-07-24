package org.example.dto.ticket;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TicketUpdateDto {
    @NotNull @Future
    private LocalDateTime dateTime;

    @NotBlank
    private String seatNumber;

    @NotNull @Positive
    private BigDecimal price;
}
