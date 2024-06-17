package com.foodiestrip.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanDto {
    private int planNo; // 플랜 번호
    private String planTitle; // 플랜 제목
    private String userId; // 사용자 ID
    private boolean planShare; // 플랜 공유 여부
    private LocalDateTime writeDate; // 작성일
    private int planBoardHit; // 조회수
    private int planBoardJjim; // 찜 수
    private String sido; // 시도 코드
    private String gugun; // 구군 코드
    private List<PlanDetailDto> planDetailDto; // 플랜 상세 정보 리스트
    private List<PlanChecklistDto> planChecklistDto; // 플랜 체크 리스트
}

