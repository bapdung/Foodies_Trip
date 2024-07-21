package com.foodiestrip.model.service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.foodiestrip.model.dao.FoodBoardDao;
import com.foodiestrip.model.dto.FoodBoardDto;
import com.foodiestrip.model.dto.FoodDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FoodBoardServiceImpl implements FoodBoardService {

	private final FoodBoardDao foodBoardDao;
	private final S3FileUploadService s3FileUploadService;

	@Override
	public void foodBoardDelete(int foodBoardNo) throws SQLException {
		foodBoardDao.foodBoardDelete(foodBoardNo);
	}

	@Override
	public void foodBoardModify(FoodBoardDto foodBoardDto) throws SQLException {
		foodBoardDao.foodBoardModify(foodBoardDto);
	}

	@Override
	public FoodBoardDto foodBoardView(int foodBoardNo) throws SQLException {
		return foodBoardDao.foodBoardView(foodBoardNo);
	}
	
	@Override
	public void modify(FoodBoardDto foodBoardDto) throws SQLException {
		foodBoardDao.foodBoardModify(foodBoardDto);
	}

	@Override
	public void foodBoardWrite(FoodBoardDto foodBoardDto) throws SQLException {
		foodBoardDao.foodBoardWrite(foodBoardDto);
	}

	@Override
	public List<FoodBoardDto> selectFoodBoardList(String sortOrder) throws SQLException {
		return foodBoardDao.selectFoodBoardList(sortOrder);
	}

	@Override
	public List<FoodBoardDto> selectFoodBoardListByTitle(String keyword) throws SQLException {
		return foodBoardDao.selectFoodBoardListByTitle(keyword);
	}

	@Override
	public int totalFoodBoardRow() throws SQLException {

		return foodBoardDao.totalFoodBoardRow();
	}

	@Override
	public void updateFoodBoardHit(int foodBoardNo) throws SQLException {
		foodBoardDao.updateFoodBoardHit(foodBoardNo);
	}

	@Override
	public void updateFoodBoardJjim(int foodBoardNo) throws SQLException {
		foodBoardDao.updateFoodBoardJjim(foodBoardNo);
	}

	@Override
	public List<FoodBoardDto> userFoodBoardList(String userId) throws SQLException {
		return foodBoardDao.userFoodBoardList(userId);
	}
	@Override
	public List<FoodDto> hotplaceSearch(int sidoCode,int gugunCode,int foodCode) throws SQLException {
		return foodBoardDao.hotplaceSearch(sidoCode,gugunCode,foodCode);
	}
	@Override
	public FoodDto getStoreDetail(int contentId) throws SQLException {
		return foodBoardDao.getStoreDetail(contentId);
	}
	
	@Override
	public List<FoodBoardDto> getStoreReviews(int contentId) throws SQLException {
		return foodBoardDao.getStoreReviews(contentId);
	}
	
	@Override
	public List<FoodBoardDto> getAuthorizedReviews(int contentId) throws SQLException {
		return foodBoardDao.getAuthorizedReviews(contentId);
	}

	//푸드보드 게시물 필터링
	@Override
	public List<FoodBoardDto> getFilteringStoreReviews(FoodDto foodDto) throws SQLException {
		return foodBoardDao.selectFoodBoardInfo(foodDto);
	}
	
	//랜덤 음식점 추천
	@Override
	public FoodDto getRandomFoodStore(FoodDto foodDto) throws SQLException {
		List<FoodDto> foodList = foodBoardDao.getRandomFoodStore(foodDto);
		int size = foodList.size();
		Random random = new Random();
		Collections.shuffle(foodList);
		int randomNumber = random.nextInt(size);
		return foodList.get(randomNumber);
	}
	
	//가게 평균 정보
	@Override
	public double[] getStatus(int contentId) throws SQLException {
		double[] arr = new double[4];
		arr[0] = Math.round(foodBoardDao.getCntReview(contentId)* 10) / 10.0;
		arr[1] = Math.round(foodBoardDao.getCntReviewMyMoney(contentId)* 10) / 10.0;
		arr[2] = Math.round(foodBoardDao.getAvgRank(contentId)* 10) / 10.0;
		arr[3] = Math.round(foodBoardDao.getAvgRankMyMoney(contentId)* 10) / 10.0;
		return arr;
	}
	
	//사진 업로드
	@Override
	public String foodBoardUploadFile(String fileName,  MultipartFile multipartFile) throws SQLException {
		try {
			if(multipartFile != null) {
				String profileURL = s3FileUploadService.saveFile(multipartFile);
				return profileURL;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		return null;
		
	}
	
}
