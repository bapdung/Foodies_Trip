package com.foodiestrip.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.foodiestrip.model.dto.UserDto;
import com.foodiestrip.model.service.UserService;
import com.foodiestrip.util.Encrypt;
//import com.foodiestrip.util.JWTUtil;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "회원 인증 컨트롤러", description = "로그인 로그아웃, 토큰처리등 회원의 인증관련 처리하는 클래스.")
@Slf4j
public class UserController {

	private final UserService userService;
	private final Encrypt encrypt;

	@Operation(summary = "로그인", description = "아이디와 비밀번호를 이용하여 로그인 처리.")
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody UserDto userDto) throws Exception {
		Map<String, String> tokens = userService.login(userDto);
		if (tokens != null) {
			return new ResponseEntity<>(tokens, HttpStatus.OK);  // 명시적으로 OK 상태 코드 설정
		}
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  // 로그인 실패 시 401 상태 코드
	}

	@Operation(summary="회원가입", description="회원정보를 입력해 회원가입 처리")
	@PostMapping("/join")
	public ResponseEntity<Map<String, String>> join(@RequestBody UserDto userDto) throws Exception {
		Map<String, String> tokens = userService.join(userDto);
		log.info("회원가입: {}",tokens);
		return ResponseEntity.ok(tokens);
	}

	@Operation(summary="회원정보수정", description="mypage에서 회원정보 수정")
	@PostMapping("/modify")
	public ResponseEntity<Map<String, Object>> modify(@RequestBody @Parameter(required =true) UserDto userDto){
		log.debug("User - modifyPOST 실행");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HttpStatus status = HttpStatus.ACCEPTED;
		try {
			String userSalt = encrypt.getSalt();
			String encryptPwd = encrypt.hashing(userDto.getUserPwd(), userSalt);

			userDto.setUserPwd(encryptPwd);
			userDto.setUserSalt(userSalt);
			userService.modify(userDto);
			status = HttpStatus.CREATED;
		} catch (Exception e) {
			log.debug("회원정보수정 중 에러 발생 : {}", e);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}

	@Operation(summary = "회원인증", description = "회원 정보를 담은 Token 을 반환한다.")
	@GetMapping("/info/{userId}")
	public ResponseEntity<Map<String, Object>> getInfo(@PathVariable("userId") String userId) {
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = HttpStatus.OK;
		try {
			UserDto userDto = userService.userDetailInfo(userId);
			resultMap.put("userInfo", userDto);
		} catch (Exception e) {
			log.error("정보조회 실패 : {}", e);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(resultMap, status);
	}

	@Operation(summary="아이디 사용가능 여부 체크", description = "아이디 사용가능 여부 체크")
	@GetMapping("/idCheck/{checkId}")
	public ResponseEntity<Map<String, Object>> idCheck(@PathVariable(name = "checkId", required = true) String checkId, HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = HttpStatus.ACCEPTED;
		log.debug("User - idCheckGET 실행");
		int rst;
		try {
			rst = userService.idCheck(checkId);
			resultMap.put("rst", rst);
		} catch (Exception e) {
			e.printStackTrace();
		}
		status = HttpStatus.OK;
		System.out.println(resultMap + " " + status);
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}

	@Operation(summary="닉네임 사용가능 여부 체크", description="악성 닉네임 방지")
	@GetMapping("/nameCheck/{userName}")
	public ResponseEntity<Map<String, Object>> nameCheck(@PathVariable(name="userName", required = true) String userName, HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = HttpStatus.ACCEPTED;
		log.debug("User - nameCheckGET 실행");
		String nameMsg = userService.nameCheck(userName);
		status = HttpStatus.OK;
		resultMap.put("nameMsg", nameMsg);
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}

	@Operation(summary = "회원탈퇴", description = "회원탈퇴")
	@GetMapping("/delete/{userId}")
	public ResponseEntity<Map<String, Object>> userDelete(@PathVariable(name="userId", required = true) String userId, HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = HttpStatus.ACCEPTED;
		log.debug("User - userDeleteGET 실행");
		try {
			userService.userDelete(userId);
		} catch (Exception e) {
			log.error("회원탈퇴 실패 : {}", e);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}

	@Operation(summary = "로그아웃", description = "회원 정보를 담은 Token 을 제거한다.")
	@GetMapping("/logout/{userId}")
	@Hidden
	public ResponseEntity<?> removeToken(@PathVariable ("userId") @Parameter(description = "로그아웃 할 회원의 아이디.", required = true) String userId) {
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = HttpStatus.ACCEPTED;
		try {
			userService.deleRefreshToken(userId);
			status = HttpStatus.OK;
		} catch (Exception e) {
			log.error("로그아웃 실패 : {}", e);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}

	// 안녕
	//안녕하세요 ??

	@Operation(summary = "Access Token 재발급", description = "만료된 access token 을 재발급 받는다.")
	@PostMapping("/refresh")
	public ResponseEntity<Map<String, String>> refresh(@RequestParam String refreshToken) throws Exception {
		Map<String, String> tokens = userService.refresh(refreshToken);
		if (tokens != null) {
			return ResponseEntity.ok(tokens);
		}
		return ResponseEntity.badRequest().body(null);
	}
}
