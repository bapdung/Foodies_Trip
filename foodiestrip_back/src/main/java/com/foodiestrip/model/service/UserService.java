package com.foodiestrip.model.service;

import com.foodiestrip.model.dto.UserDto;
import com.foodiestrip.model.dto.UserStoredPlanDto;

public interface UserService {
	UserDto login(UserDto userDto) throws Exception;
	
	void join(UserDto userDto) throws Exception;
	
	void modify(UserDto userDto) throws Exception;
	
	void userDelete(String userId) throws Exception;

	UserDto userInfo(String userId) throws Exception;

	UserDto userDetailInfo(String userId) throws Exception;

	void saveRefreshToken(String userId, String refreshToken) throws Exception;

	Object getRefreshToken(String userId) throws Exception;

	void deleRefreshToken(String userId) throws Exception;

	int idCheck(String userId) throws Exception;

	String nameCheck(String userName);
}
