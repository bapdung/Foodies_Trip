package com.foodiestrip.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserStoredStoreDto {
    private int userStoredStoreNo; // 사용자 저장 가게 번호
    private String userId; // 사용자 ID
    private int contentId; // 가게 정보 ID
}
