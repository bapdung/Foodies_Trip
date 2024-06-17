package com.foodiestrip.model.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.foodiestrip.model.dto.UserDto;
import com.foodiestrip.model.dto.UserPreferenceDto;

@Mapper
public interface UserDao {
	UserDto login(UserDto userDto) throws SQLException;

	void join(UserDto userDto) throws SQLException;

	UserDto userInfo(String userId) throws SQLException;

	void saveRefreshToken(Map<String, String> map) throws SQLException;

	Object getRefreshToken(String userid) throws SQLException;

	void deleteRefreshToken(Map<String, String> map) throws SQLException;

	void insertUserPreference(UserPreferenceDto userPreferenceDto);

	void deleteUserPreference(String userId);

	void updateUserPreference(UserPreferenceDto userPreferenceDto);

	void updateUserInfo(UserDto userDto);
	
	void deleteUserInfo(String userId);

	int idCheck(String userId);

	UserPreferenceDto selectUserPreferenceByUserId(String userId);

	List<UserPreferenceDto> selectAllUserPreferences();
}
