package com.foodiestrip.model.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.foodiestrip.model.dao.UserDao;
import com.foodiestrip.model.dao.UserStoredPlanDao;
import com.foodiestrip.model.dto.UserDto;
import com.foodiestrip.model.dto.UserPreferenceDto;
import com.foodiestrip.model.dto.UserStoredPlanDto;
import com.foodiestrip.util.KMP;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserDao userDao;
	private final KMP kmp;

	@Override
	public UserDto login(UserDto userDto) throws Exception {
		return userDao.login(userDto);
	}
	
	@Override
	public void join(UserDto userDto) throws Exception {
		userDao.join(userDto);
		userDao.insertUserPreference(userDto.getUserPreferenceDto());
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
	public void saveRefreshToken(String userId, String refreshToken) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("userId", userId);
		map.put("token", refreshToken);
		userDao.saveRefreshToken(map);
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

}
