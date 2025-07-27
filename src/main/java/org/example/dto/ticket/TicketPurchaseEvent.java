package org.example.dto.ticket;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TicketPurchaseEvent {
    private Long ticketId;
    private Long userId;
    private BigDecimal price;
    private Long routeId;
    private LocalDateTime dateTime;
    private String seatNumber;
}
