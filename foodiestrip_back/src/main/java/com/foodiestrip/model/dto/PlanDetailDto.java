package com.foodiestrip.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanDetailDto {
    private int planDetailNo; // 계획 상세 정보 번호
    private int planNo; // 플랜 번호
    private String address;
    private int contentId; // 컨텐츠 ID
    private int routeDay; // 날짜
    private int routeNo; // 계획 순서
    private String title; // 장소 제목
    private String detail; // 세부 계획
}