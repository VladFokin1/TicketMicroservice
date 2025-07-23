package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    private Integer id;
    private Integer userId;
    private String token;
    private LocalDateTime expiryDate;
}
