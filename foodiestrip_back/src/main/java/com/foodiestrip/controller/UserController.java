package com.foodiestrip.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodiestrip.model.dto.UserDto;
import com.foodiestrip.model.service.UserService;
import com.foodiestrip.util.Encrypt;
import com.foodiestrip.util.JWTUtil;

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
	private final JWTUtil jwtUtil;

	@Operation(summary = "로그인", description = "아이디와 비밀번호를 이용하여 로그인 처리.")
	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(
			@RequestBody @Parameter(description = "로그인 시 필요한 회원정보(아이디, 비밀번호).", required = true) UserDto userDto) {
		System.out.println("로그인실행");
		log.debug("login user : {}", userDto);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HttpStatus status = HttpStatus.ACCEPTED;
		try {
			
			UserDto loginUser = userService.login(userDto);
			
			if(loginUser != null) {
				String encryptPwd = encrypt.hashing(userDto.getUserPwd(), loginUser.getUserSalt());
				//암호화된 비번이랑 틀렸을 때
				String orgPwd = loginUser.getUserPwd();
				if (!orgPwd.equals(encryptPwd)) {
					resultMap.put("message", "아이디 또는 패스워드를 확인해 주세요.");
					status = HttpStatus.UNAUTHORIZED;
				} else {
					String accessToken = jwtUtil.createAccessToken(loginUser.getUserId());
					String refreshToken = jwtUtil.createRefreshToken(loginUser.getUserId());
					log.debug("access token : {}", accessToken);
					log.debug("refresh token : {}", refreshToken);

//				발급받은 refresh token 을 DB에 저장.
					userService.saveRefreshToken(loginUser.getUserId(), refreshToken);

//				JSON 으로 token 전달.
					resultMap.put("access-token", accessToken);
					resultMap.put("refresh-token", refreshToken);

					status = HttpStatus.CREATED;
				}
			} else {
				resultMap.put("message", "아이디 또는 패스워드를 확인해 주세요.");
				status = HttpStatus.UNAUTHORIZED;
			} 
			
		} catch (Exception e) {
			log.debug("로그인 에러 발생 : {}", e);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}
	
	@Operation(summary="회원가입", description="회원정보를 입력해 회원가입 처리")
	@PostMapping("/join")
	public ResponseEntity<Map<String, Object>> join(@RequestBody @Parameter(required =true) UserDto userDto){
		log.debug("User - joinPOST 실행");
		System.out.println("join");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HttpStatus status = HttpStatus.ACCEPTED;
		try {
			String userSalt = encrypt.getSalt();
			String encryptPwd = encrypt.hashing(userDto.getUserPwd(), userSalt);

			userDto.setUserPwd(encryptPwd);
			userDto.setUserSalt(userSalt);
			
			userService.join(userDto);
			
			status = HttpStatus.CREATED;
		} catch (Exception e) {
			log.debug("회원가입 에러 발생 : {}", e);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
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
	public ResponseEntity<Map<String, Object>> getInfo(
			@PathVariable("userId") @Parameter(description = "인증할 회원의 아이디.", required = true) String userId,
			HttpServletRequest request) {
//		logger.debug("userId : {} ", userId);
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = HttpStatus.ACCEPTED;
		if (jwtUtil.checkToken(request.getHeader("Authorization"))) {
			log.info("사용 가능한 토큰!!!");
			try {
//				로그인 사용자 정보.
				UserDto userDto = userService.userDetailInfo(userId);
				resultMap.put("userInfo", userDto);
//				System.out.println(resultMap);
				status = HttpStatus.OK;
			} catch (Exception e) {
				log.error("정보조회 실패 : {}", e);
				resultMap.put("message", e.getMessage());
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		} else {
			log.error("사용 불가능 토큰!!!");
			status = HttpStatus.UNAUTHORIZED;
		}
		System.out.println(resultMap + " " + status);
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
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
	//안녕하세요
	
	@Operation(summary = "Access Token 재발급", description = "만료된 access token 을 재발급 받는다.")
	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@RequestBody UserDto userDto, HttpServletRequest request)
			throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = HttpStatus.ACCEPTED;
		String token = request.getHeader("refreshToken");
		log.debug("token : {}, memberDto : {}", token, userDto);
		if (jwtUtil.checkToken(token)) {
			if (token.equals(userService.getRefreshToken(userDto.getUserId()))) {
				String accessToken = jwtUtil.createAccessToken(userDto.getUserId());
				log.debug("token : {}", accessToken);
				log.debug("정상적으로 access token 재발급!!!");
				resultMap.put("access-token", accessToken);
				status = HttpStatus.CREATED;
			}
		} else {
			log.debug("refresh token 도 사용 불가!!!!!!!");
			status = HttpStatus.UNAUTHORIZED;
		}
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}
}
