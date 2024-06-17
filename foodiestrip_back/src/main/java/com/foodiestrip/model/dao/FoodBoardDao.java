package com.foodiestrip.model.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.foodiestrip.model.dto.FoodBoardDto;
import com.foodiestrip.model.dto.FoodDto;

@Mapper
public interface FoodBoardDao {
	// 키워드로 음식 게시물 목록 조회
	List<FoodBoardDto> selectFoodBoardListByTitle(String keyword) throws SQLException;

	// 음식 게시물 작성
	void foodBoardWrite(FoodBoardDto foodBoardDto) throws SQLException;

	// 특정 음식 게시물 조회
	FoodBoardDto foodBoardView(int foodBoardNo) throws SQLException;

	// 음식 게시물 목록 조회
	List<FoodBoardDto> selectFoodBoardList(String sortOrder) throws SQLException;

	// 음식 게시물 총 개수 조회
	int totalFoodBoardRow() throws SQLException;

	// 음식 게시물 수정
	void foodBoardModify(FoodBoardDto foodBoardDto) throws SQLException;

	// 음식 게시물 조회수 증가
	void updateFoodBoardHit(int foodBoardNo) throws SQLException;

	// 음식 게시물 찜 개수 증가
	void updateFoodBoardJjim(int foodBoardNo) throws SQLException;

	// 음식 게시물 삭제
	void foodBoardDelete(int foodBoardNo) throws SQLException;

	// 내가 작성한 음식 게시물 목록 조회
	List<FoodBoardDto> userFoodBoardList(String userId) throws SQLException;

	// 맛집 검색
	List<FoodDto> hotplaceSearch(int sidoCode, int gugunCode, int foodCode) throws SQLException;

	// 맛집 상세조회
	FoodDto getStoreDetail(int contentId) throws SQLException;

	// 특정 맛집 리뷰 조회
	List<FoodBoardDto> getStoreReviews(int contentId) throws SQLException;

	// 특정 맛집 인증된 리뷰 조회
	List<FoodBoardDto> getAuthorizedReviews(int contentId) throws SQLException;

	List<FoodBoardDto> selectFoodBoardInfo(FoodDto foodDto) throws SQLException;

	List<FoodDto> getRandomFoodStore(FoodDto foodDto) throws SQLException;

	Float getAvgRankMyMoney(int contentId) throws SQLException;

	Float getAvgRank(int contentId) throws SQLException;

	Integer getCntReviewMyMoney(int contentId) throws SQLException;

	Integer getCntReview(int contentId) throws SQLException;
}
