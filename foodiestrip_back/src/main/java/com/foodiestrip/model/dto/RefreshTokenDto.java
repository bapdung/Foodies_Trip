package com.foodiestrip.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenDto {
    private Long id;
    private Long userId;
    private String refreshToken;
}
