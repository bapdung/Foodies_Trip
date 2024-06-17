package com.foodiestrip.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPreferenceDto {
    private int userPreferenceNo; // 사용자 선호 정보 번호
    private String userId; // 사용자 ID
    private boolean mountainPref; // 산 좋아하는지 여부
    private boolean seaPref; // 바다 좋아하는지 여부
    private boolean tourPref; // 여행 좋아하는지 여부
    private boolean festivalPref; // 축제 좋아하는지 여부
    private boolean sportPref; // 스포츠 좋아하는지 여부
    private boolean shoppingPref; // 쇼핑 좋아하는지 여부
    private boolean foodPref; // 음식 좋아하는지 여부
}
