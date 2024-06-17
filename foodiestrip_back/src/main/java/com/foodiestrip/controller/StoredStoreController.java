package com.foodiestrip.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodiestrip.model.dto.FoodDto;
import com.foodiestrip.model.dto.UserStoredStoreDto;
import com.foodiestrip.model.service.UserStoredStoreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/mystore")
@RequiredArgsConstructor
public class StoredStoreController {
	private final UserStoredStoreService storeService;
	
	@PostMapping("/store")
	ResponseEntity<?>insertUserStoredStore(@RequestBody UserStoredStoreDto userStoredStoreDto){
		System.out.println(userStoredStoreDto.toString());
		try {
			Map<String, Object> response = new HashMap<>();
				storeService.insertUserStoredStore(userStoredStoreDto);
				response.put("color", "yellow");
			return ResponseEntity.ok(response);
		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}
	
	@DeleteMapping("/store")
	ResponseEntity<?>deleteUserStoredStore(@RequestBody UserStoredStoreDto userStoredStoreDto){
		try {
			Map<String, Object> response = new HashMap<>();
			storeService.deleteUserStoredStore(userStoredStoreDto);
			response.put("color", "gray");
			return ResponseEntity.ok(response);
		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}
	
	//user가 저장한 맛집 게시물
	@GetMapping("/store")
	ResponseEntity<?>selectAllUserStoredStores(@RequestParam("userId") String userId){
		try {
			Map<String, Object> response = new HashMap<>();
			List<FoodDto> myLikeList = storeService.selectAllUserStoredStores(userId);
			response.put("myLikeList", myLikeList);
			return ResponseEntity.ok(response);
		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}
	
	@PostMapping("/can")
	ResponseEntity<?>canStore(@RequestBody UserStoredStoreDto userStoredStoreDto){
		try {
			Map<String, Object> response = new HashMap<>();
				int cnt = storeService.storedstoreCheck(userStoredStoreDto);
				if(cnt == 0) {
					response.put("color", "gray");
				} else {
					response.put("color", "yellow");
				}
			return ResponseEntity.ok(response);
		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}
	
}
