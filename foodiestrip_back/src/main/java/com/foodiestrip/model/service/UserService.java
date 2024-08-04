package com.foodiestrip.model.service;

import com.foodiestrip.model.dto.UserDto;

import java.util.Map;

public interface UserService {
	Map<String, String> login(UserDto userDto) throws Exception;
	
	Map<String, String> join(UserDto userDto) throws Exception;
	
	void modify(UserDto userDto) throws Exception;
	
	void userDelete(String userId) throws Exception;

	UserDto userInfo(String userId) throws Exception;

	UserDto userDetailInfo(String userId) throws Exception;

	void saveRefreshToken(String userId, String refreshToken) throws Exception;

	Map<String, String> refresh(String refreshToken) throws Exception;

	Object getRefreshToken(String userId) throws Exception;

	void deleRefreshToken(String userId) throws Exception;

	int idCheck(String userId) throws Exception;

	String nameCheck(String userName);

	//jwt 토큰 처리를 위해 생성

}
