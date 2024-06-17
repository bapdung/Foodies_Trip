package com.foodiestrip.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodBoardDto {
    private int foodBoardNo; // 맛슐랭 게시판 번호
    private int contentId; // 맛집 번호
    private String userId; // 사용자 ID
    private String foodStoreTitle; // 가게 제목
    private String foodBoardReview; // 리뷰 내용
    private int foodBoardHit; // 조회수
    private int foodBoardJjim; // 찜 수
    private float foodBoardRank; // 평점
    private boolean foodBoardMyMoney; // 내 돈 주고 싶은지 여부
    private String foodBoardAddr; // 가게 주소
    private String foodBoardReceiptImage; // 영수증 이미지
    private String foodBoardImage; // 음식 이미지 (Base64 문자열)
    private LocalDateTime writeDate; // 작성일
    private int sidoCode;
    private int gugunCode;
    private int contentTypeId;
    private String contentTypeName;
}
