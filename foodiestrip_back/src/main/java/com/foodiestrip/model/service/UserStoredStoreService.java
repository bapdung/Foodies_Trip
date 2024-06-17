package com.foodiestrip.model.service;

import java.sql.SQLException;
import java.util.List;

import com.foodiestrip.model.dto.FoodDto;
import com.foodiestrip.model.dto.UserStoredStoreDto;

public interface UserStoredStoreService {
	// 사용자 저장된 상점 삽입
	void insertUserStoredStore(UserStoredStoreDto userStoredStoreDto) throws SQLException;

	// 사용자 저장된 상점 삭제
	void deleteUserStoredStore(UserStoredStoreDto userStoredStoreDto) throws SQLException;

	// 특정 사용자의 모든 저장된 상점 조회
	List<FoodDto> selectAllUserStoredStores(String userId) throws SQLException;

	// 저장되 있는거 또저장 못하게
	int storedstoreCheck(UserStoredStoreDto userStoredStoreDto) throws SQLException;
}
