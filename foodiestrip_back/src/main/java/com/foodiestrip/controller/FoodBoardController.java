package com.foodiestrip.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.foodiestrip.model.dto.FoodBoardDto;
import com.foodiestrip.model.dto.FoodDto;
import com.foodiestrip.model.service.FoodBoardService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/foodboard")
@RequiredArgsConstructor
public class FoodBoardController {
	private final FoodBoardService foodBoardService;

	@GetMapping("/hotplace")
	ResponseEntity<?> getSearchHotplace(@RequestParam(value = "sidoCode") String sidoCode,
			@RequestParam(value = "gugunCode") String gugunCode, @RequestParam(value = "foodCode") String foodCode) {
		try {
			Map<String, Object> response = new HashMap<>();
			int sido = Integer.parseInt(sidoCode);
			int gugun = Integer.parseInt(gugunCode);
			int food = Integer.parseInt(foodCode);
			List<FoodDto> hotplaceList = foodBoardService.hotplaceSearch(sido, gugun, food);
			response.put("hotplaceList", hotplaceList);
			return ResponseEntity.ok(response);

		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}

	@GetMapping("/getDetail")
	ResponseEntity<?> getFoodStoreDetail(@RequestParam(value = "contentId") String contentId) {
		try {
			Map<String, Object> response = new HashMap<>();
			int contentid = Integer.parseInt(contentId);
			FoodDto foodStore = foodBoardService.getStoreDetail(contentid);
			response.put("foodStore", foodStore);
			return ResponseEntity.ok(response);

		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}
	
	@GetMapping("/detail/{foodBoardNo}")
	ResponseEntity<?> getfoodBoardView(@PathVariable(value="foodBoardNo")String foodBoardNo){
		try {
			Map<String, Object> response = new HashMap<>();
			int foodBoardno = Integer.parseInt(foodBoardNo);
			FoodBoardDto foodboarddetail = foodBoardService.foodBoardView(foodBoardno);
			response.put("reviewDetail", foodboarddetail);
			return ResponseEntity.ok(response);

		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}
	
	@GetMapping("/listall")
	ResponseEntity<?> selectFoodBoardList(@RequestParam(value = "sortOrder") String sortOrder) {
		System.out.println(sortOrder);
		try {
			Map<String, Object> response = new HashMap<>();
			List<FoodBoardDto> allList = foodBoardService.selectFoodBoardList(sortOrder);
			response.put("allList", allList);
			return ResponseEntity.ok(response);

		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}

	@GetMapping("/list")
	ResponseEntity<?> selectFoodBoardListByTitle(@RequestParam("keyward") String keyward) {
		try {
			Map<String, Object> response = new HashMap<>();
			List<FoodBoardDto> keywardSearchList = foodBoardService.selectFoodBoardListByTitle(keyward);
			response.put("keywardSearchList", keywardSearchList);
			return ResponseEntity.ok(response);

		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}

	@GetMapping("/count")
	ResponseEntity<?> totalFoodBoardRow() {
		try {
			Map<String, Object> response = new HashMap<>();
			int value = foodBoardService.totalFoodBoardRow();
			response.put("boardCount", value);
			return ResponseEntity.ok(response);
		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}
	
	@PostMapping("/board")
	public ResponseEntity<?> foodBoardWrite(@RequestBody FoodBoardDto foodBoardDto, @RequestPart(value="multifile", required=false) final MultipartFile multipartFile) {
	    System.out.println(foodBoardDto.toString());
	    try {
	        foodBoardService.foodBoardWrite(foodBoardDto);
	        foodBoardService.foodBoardUploadFile(foodBoardDto.getFoodStoreTitle(), multipartFile);
	        return ResponseEntity.ok().build();
	    } catch (SQLException e) {
	        return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
	    } catch (Exception e) {
	        // 일반적인 예외 처리를 추가하여 예상치 못한 오류도 처리
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
	    }
	}

	
	@PatchMapping("/board")
	ResponseEntity<?> foodBoardModify(@RequestBody FoodBoardDto foodBoardDto) {
		try {
			foodBoardService.foodBoardModify(foodBoardDto);
			return ResponseEntity.ok().build();
		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}
	
	@DeleteMapping("/board")
	ResponseEntity<?> foodBoardDelete(@RequestParam("foodBoardNo") String foodBoardNo){
		try {
			int boardNo = Integer.parseInt(foodBoardNo);
			foodBoardService.foodBoardDelete(boardNo);
			return ResponseEntity.ok().build();
		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}
	
	@GetMapping("/hit")
	ResponseEntity<?> updateFoodBoardHit(@RequestParam("foodBoardNo") String foodBoardNo){
		try {
			int boardNo = Integer.parseInt(foodBoardNo);
			foodBoardService.updateFoodBoardHit(boardNo);
			return ResponseEntity.ok().build();
		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}
	
	@GetMapping("/jjim")
	ResponseEntity<?> updateFoodBoardJjim(@RequestParam("foodBoardNo") String foodBoardNo){
		try {
			int boardNo = Integer.parseInt(foodBoardNo);
			foodBoardService.updateFoodBoardJjim(boardNo);
			return ResponseEntity.ok().build();
		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}
	
	@GetMapping("/mylist")
	ResponseEntity<?> userFoodBoardList(@RequestParam(value = "userId") String userId) {
		try {
			Map<String, Object> response = new HashMap<>();		
			List<FoodBoardDto> myList = foodBoardService.userFoodBoardList(userId);
			response.put("myList", myList);
			return ResponseEntity.ok(response);
			
		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}
	
	@GetMapping("/reviews")
	ResponseEntity<?> getStoreReviews(@RequestParam(value = "contentId") String contentId) {
		log.info("contentId: {}",contentId);
		try {
			int contentid = Integer.parseInt(contentId);
			Map<String, Object> response = new HashMap<>();		
			List<FoodBoardDto> contentReviewList = foodBoardService.getStoreReviews(contentid);
			response.put("reviewList", contentReviewList);
			return ResponseEntity.ok(response);
			
		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}
	
	//인증된 리뷰
	@GetMapping("/authorize")
	ResponseEntity<?> getAuthorizedReviews(@RequestParam(value = "contentId") String contentId) {
		try {
			int contentid = Integer.parseInt(contentId);
			Map<String, Object> response = new HashMap<>();		
			List<FoodBoardDto> contentReviewList = foodBoardService.getAuthorizedReviews(contentid);
			response.put("reviewList", contentReviewList);
			return ResponseEntity.ok(response);
			
		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}
	
	//평균값, 개수 구하기
	@GetMapping("/average")
	ResponseEntity<?> getStatus(@RequestParam(value = "contentId") String contentId) {
		try {
			int contentid = Integer.parseInt(contentId);
			Map<String, Object> response = new HashMap<>();		
			double[] arr = foodBoardService.getStatus(contentid);
			response.put("countReivew", arr[0]);
			response.put("countReivewAuthorized", arr[1]);
			response.put("avgRank", arr[2]);
			response.put("avgRankAuthorized", arr[3]);
			return ResponseEntity.ok(response);
		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}
	
	@Operation(summary = "필터링해서 검색하기")
	@PostMapping("/search")
	public ResponseEntity<?> searchfoodBoard(@RequestBody FoodDto foodDto) {
		
	    try {
	    	Map<String, Object> response = new HashMap<>();
	    	List<FoodBoardDto> reviewList = foodBoardService.getFilteringStoreReviews(foodDto);
	    	response.put("reviewList", reviewList);
	        return ResponseEntity.ok(response);
	    } catch (SQLException e) {
	        return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
	    } catch (Exception e) {
	        // 일반적인 예외 처리를 추가하여 예상치 못한 오류도 처리
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
	    }
	}
	
	@Operation(summary = "랜덤 가게 검색하기")
	@PostMapping("/random")
	public ResponseEntity<?> foodRandom(@RequestBody FoodDto foodDto) {
		System.out.println(foodDto.toString());
	    try {
	    	Map<String, Object> response = new HashMap<>();
	    	FoodDto food = foodBoardService.getRandomFoodStore(foodDto);
	    	response.put("store", food);
	    	System.out.println(food);
	        return ResponseEntity.ok(response);
	    } catch (SQLException e) {
	        return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
	    } catch (Exception e) {
	        // 일반적인 예외 처리를 추가하여 예상치 못한 오류도 처리
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
	    }
	}
	
	@Operation(summary = "게시글 수정")
	@PostMapping("/modify")
	public ResponseEntity<?> modify(@RequestBody FoodBoardDto foodBoardDto) {
		System.out.println(foodBoardDto.toString());
	    try {
	    	Map<String, Object> response = new HashMap<>();
	    	foodBoardService.modify(foodBoardDto);
	        return ResponseEntity.ok(response);
	    } catch (SQLException e) {
	        return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
	    } catch (Exception e) {
	        // 일반적인 예외 처리를 추가하여 예상치 못한 오류도 처리
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
	    }
	}
	
	
	
}
