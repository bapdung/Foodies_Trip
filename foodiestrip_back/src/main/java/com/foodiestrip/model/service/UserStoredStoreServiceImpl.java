package com.foodiestrip.model.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.foodiestrip.model.dao.FoodBoardDao;
import com.foodiestrip.model.dao.UserStoredStoreDao;
import com.foodiestrip.model.dto.FoodDto;
import com.foodiestrip.model.dto.UserStoredStoreDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserStoredStoreServiceImpl implements UserStoredStoreService {
	private final UserStoredStoreDao storeDao;
	private final FoodBoardDao foodBoardDao;

	@Override
	public void deleteUserStoredStore(UserStoredStoreDto userStoredStoreDto) throws SQLException {
		storeDao.deleteUserStoredStore(userStoredStoreDto);

	}

	@Override
	public void insertUserStoredStore(UserStoredStoreDto userStoredStoreDto) throws SQLException {
		storeDao.insertUserStoredStore(userStoredStoreDto);

	}

	@Override
	public List<FoodDto> selectAllUserStoredStores(String userId) throws SQLException {
		List<UserStoredStoreDto> list = storeDao.selectAllUserStoredStores(userId);
		List<FoodDto> storeList = new ArrayList<>();
		if (list != null) {
			for (UserStoredStoreDto storedStore : list) {
				storeList.add(foodBoardDao.getStoreDetail(storedStore.getContentId()));
			}
		}
		return storeList;

	}

	@Override
	public int storedstoreCheck(UserStoredStoreDto userStoredStoreDto) throws SQLException {
		return storeDao.storedstoreCheck(userStoredStoreDto);
	}
}
