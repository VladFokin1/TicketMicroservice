package org.example.dto.route;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RouteCreateDto {
    @NotBlank @Size(min = 2, max = 100)
    private String departurePoint;

    @NotBlank @Size(min = 2, max = 100)
    private String destinationPoint;

    @NotNull @Min(1)
    private Integer carrierId;

    @NotNull @Positive
    private Integer durationMinutes;
}