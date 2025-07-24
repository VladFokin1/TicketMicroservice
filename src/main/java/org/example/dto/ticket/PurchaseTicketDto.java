package org.example.dto.ticket;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PurchaseTicketDto {
    @Min(value = 1, message = "ID билета должно быть положительным числом")
    private Long ticketId;

    @Min(value = 1, message = "ID пользователя должно быть положительным числом")
    private Long userId;
}