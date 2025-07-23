package org.example.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Ticket {
    private Integer id;
    private Integer routeId;
    private LocalDateTime dateTime;
    private String seatNumber;
    private BigDecimal price;
    private Integer userId; // null если не куплен
}
