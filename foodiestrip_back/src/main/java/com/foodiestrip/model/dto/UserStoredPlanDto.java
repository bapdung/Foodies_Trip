package com.foodiestrip.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserStoredPlanDto {
    private int userStoredPlanNo; // 사용자 저장 플랜 번호
    private String userId; // 사용자 ID
    private int planNo; // 플랜 번호
}