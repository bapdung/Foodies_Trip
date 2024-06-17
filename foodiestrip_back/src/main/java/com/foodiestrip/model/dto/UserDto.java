package com.foodiestrip.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // 게터, 세터, `toString()`, `equals()`, `hashCode()` 메서드를 생성합니다.
@AllArgsConstructor // 모든 필드를 가진 생성자를 생성합니다.
@NoArgsConstructor // 인자 없는 생성자를 생성합니다.
@Builder // 빌더 패턴을 구현합니다.
public class UserDto {
    private String userId; // 사용자 ID
    private String userPwd; // 사용자 비밀번호
    private String userSalt; // 비밀번호 솔트
    private String userName; // 사용자 이름
    private String userNickname; // 사용자 닉네임
    private String emailId; // 이메일 아이디
    private String emailDomain; // 이메일 도메인
    private String sido; // 시/도 정보
    private String gugun; // 구/군 정보
    private String refreshToken; // 리프레시 토큰
    private boolean isAdmin; // 관리자 여부
    private UserPreferenceDto userPreferenceDto; 
}
