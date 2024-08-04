package com.foodiestrip.model.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.foodiestrip.config.jwt.TokenProvider;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodiestrip.model.dao.UserDao;
import com.foodiestrip.model.dto.UserDto;
import com.foodiestrip.model.dto.UserPreferenceDto;
import com.foodiestrip.util.KMP;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {
	
	private final UserDao userDao;
	private final KMP kmp;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;

	@Override
	public Map<String, String> login(UserDto userDto) throws Exception {
		log.info("login 시도: {}",userDto);
		UserDto foundUser = userDao.login(userDto);
		if (foundUser != null && passwordEncoder.matches(userDto.getUserPwd(), foundUser.getUserPwd())) {
			String accessToken = tokenProvider.generateToken(foundUser, Duration.ofHours(2));
			String refreshToken = tokenProvider.generateToken(foundUser, Duration.ofDays(14));

			// RefreshToken 저장 또는 업데이트
			saveRefreshToken(foundUser.getUserId(), refreshToken);

			Map<String, String> tokens = new HashMap<>();
			tokens.put("accessToken", accessToken);
			tokens.put("refreshToken", refreshToken);
			log.info("로그인 성공");
			return tokens;
		}
		log.info("로그인 실패");
		return null;
	}

	@Override
	@Transactional
	public Map<String, String> join(UserDto userDto) throws Exception {
		// 비밀번호 암호화
		userDto.setUserPwd(passwordEncoder.encode(userDto.getUserPwd()));

		// 회원 정보 저장
		userDao.join(userDto);
		userDao.insertUserPreference(userDto.getUserPreferenceDto());

		// JWT 토큰 생성
		String accessToken = tokenProvider.generateToken(userDto, Duration.ofHours(2));
		String refreshToken = tokenProvider.generateToken(userDto, Duration.ofDays(14));

		// RefreshToken 저장
		saveRefreshToken(userDto.getUserId(), refreshToken);

		Map<String, String> tokens = new HashMap<>();
		tokens.put("accessToken", accessToken);
		tokens.put("refreshToken", refreshToken);
		log.info("accessToken: " + accessToken);
		log.info("refreshToken: " + refreshToken);
		return tokens;
	}
	
	@Override
	public UserDto userInfo(String userId) throws Exception {
		return userDao.userInfo(userId);
	}

	@Override
	public UserDto userDetailInfo(String userId) throws Exception {
		UserDto userDto = userDao.userInfo(userId);
		UserPreferenceDto userPreferenceDto = userDao.selectUserPreferenceByUserId(userId);
		userDto.setUserPreferenceDto(userPreferenceDto);
		return userDto;
	}
	@Override
	public Map<String, String> refresh(String refreshToken) throws Exception {
		if (tokenProvider.validToken(refreshToken)) {
			String userId = tokenProvider.getUserIdFromToken(refreshToken);
			String storedRefreshToken = (String) getRefreshToken(userId);

			if (refreshToken.equals(storedRefreshToken)) {
				UserDto userDto = userInfo(userId);
				String newAccessToken = tokenProvider.generateToken(userDto, Duration.ofHours(2));

				Map<String, String> tokens = new HashMap<>();
				tokens.put("accessToken", newAccessToken);
				return tokens;
			}
		}
		return null;
	}

	@Override
	public Object getRefreshToken(String userId) throws Exception {
		return userDao.getRefreshToken(userId);
	}

	@Override
	public void deleRefreshToken(String userId) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("userId", userId);
		map.put("token", null);
		userDao.deleteRefreshToken(map);
	}
	
	@Override
	public int idCheck(String userId) {
		System.out.println("idCheck Service 실행");
		return userDao.idCheck(userId);
	}
	
	@Override
	public String nameCheck(String userName) {
		String result = kmp.findSpam(userName);
		return result;
	}

	@Override
	public void modify(UserDto userDto) throws Exception {
		userDao.updateUserInfo(userDto);
		userDao.updateUserPreference(userDto.getUserPreferenceDto());
		
	}

	@Override
	public void userDelete(String userId) throws Exception {
		userDao.deleteUserPreference(userId);
		userDao.deleteUserInfo(userId);
	}


	@Override
	public void saveRefreshToken(String userId, String refreshToken) throws Exception {
		try {
			Map<String, String> map = new HashMap<>();
			map.put("userId", userId);
			map.put("token", refreshToken);
			userDao.saveRefreshToken(map);
			log.info("RefreshToken saved successfully for user: {}", userId);
		} catch (Exception e) {
			log.error("Failed to save RefreshToken for user: {}. Error: {}", userId, e.getMessage());
			throw new Exception("RefreshToken 저장 중 오류 발생", e);
		}
	}

}
