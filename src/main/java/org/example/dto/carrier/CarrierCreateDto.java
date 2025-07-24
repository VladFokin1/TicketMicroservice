package org.example.dto.carrier;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CarrierCreateDto {
    @NotBlank @Size(min = 2, max = 100)
    private String name;

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9\\s-]{10,}$")
    private String phone;
}
