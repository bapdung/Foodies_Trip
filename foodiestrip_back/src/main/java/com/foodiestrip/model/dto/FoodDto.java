package com.foodiestrip.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // 게터, 세터, `toString()`, `equals()`, `hashCode()` 메서드를 생성합니다.
@AllArgsConstructor // 모든 필드를 가진 생성자를 생성합니다.
@NoArgsConstructor // 인자 없는 생성자를 생성합니다.
@Builder // 빌더 패턴을 구현합니다.
public class FoodDto {
	private int contentId;
    private int contentTypeId;
    private String title;
    private String addr1;
    private String addr2;
    private String tel;
    private int sidoCode;
    private int gugunCode;
    private double latitude;
    private double longitude;
    private String contentTypeName;
}
