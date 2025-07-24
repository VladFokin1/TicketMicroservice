package org.example.dto.ticket;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class TicketFilterDto {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateTime;

    @Size(min = 2, max = 100, message = "Пункт отправления должен быть от 2 до 100 символов")
    private String departurePoint;

    @Size(min = 2, max = 100, message = "Пункт назначения должен быть от 2 до 100 символов")
    private String destinationPoint;

    @Size(min = 2, max = 100, message = "Название перевозчика должно быть от 2 до 100 символов")
    private String carrierName;

    private Long userId; // Для проверки существования пользователя
}