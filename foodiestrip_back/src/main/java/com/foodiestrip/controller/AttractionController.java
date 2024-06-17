package com.foodiestrip.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.foodiestrip.model.dto.AttractionDto;
import com.foodiestrip.model.dto.GugunDto;
import com.foodiestrip.model.dto.SidoDto;
import com.foodiestrip.model.service.AttractionService;

import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/attraction")
@RequiredArgsConstructor
public class AttractionController {
	private final AttractionService attractionService;

	@GetMapping("/view")
	public ResponseEntity<?> viewGet() {
		try {
			List<SidoDto> sidoList = attractionService.sidoList();
			List<GugunDto> gugunList = attractionService.gugunList(1);
			Map<String, Object> response = new HashMap<>();
			response.put("sidoList", sidoList);
			response.put("gugunList", gugunList);
			return ResponseEntity.ok(response);
		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}

	@GetMapping("/gugun/{sidoCode}")
	public ResponseEntity<?> gugunGetPath(@PathVariable(value = "sidoCode", required = true) String sidoCode) {
		System.out.println(sidoCode);
		try {
			Map<String, Object> response = new HashMap<>();
			int sido = Integer.parseInt(sidoCode);
			List<GugunDto> gugunList = attractionService.gugunList(sido);
			response.put("gugunList", gugunList);
			return ResponseEntity.ok(response);
		} catch (NumberFormatException e) {
			return ResponseEntity.badRequest().body("잘못된 시도 코드 형식입니다.");
		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}

	@GetMapping("/recommend")
	ResponseEntity<?> recommendGet(@RequestParam(value = "sidoCode", required = true) String sidoCode) {
		try {
			Map<String, Object> response = new HashMap<>();
			int sido = Integer.parseInt(sidoCode);

			List<AttractionDto> recommendList = attractionService.recommendList(sido);
			response.put("recommendList", recommendList);

			return ResponseEntity.ok(response);
		} catch (NumberFormatException e) {
			return ResponseEntity.badRequest().body("잘못된 시도 코드 형식입니다.");
		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}

	@PostMapping("/search")
	ResponseEntity<?> attractionList(@RequestBody AttractionDto attractionDto) {
		System.out.println(attractionDto);
		try {
			Map<String, Object> response = new HashMap<>();

			List<AttractionDto> attractionList = attractionService.attractionList(attractionDto);
			response.put("attractionList", attractionList);
			System.out.println(response);

			return ResponseEntity.ok(response);
		} catch (NumberFormatException e) {
			return ResponseEntity.badRequest().body("잘못된 시도 코드 형식입니다.");
		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}
	
	@GetMapping("/detail/{contentId}")
	ResponseEntity<?> recommendGet(@PathVariable(value = "contentId", required = true) int contentId) {
		try {
			Map<String, Object> response = new HashMap<>();
			AttractionDto attraction = attractionService.getAttraction(contentId);
			response.put("attraction", attraction);

			return ResponseEntity.ok(response);
		} catch (NumberFormatException e) {
			return ResponseEntity.badRequest().body("잘못된 시도 코드 형식입니다.");
		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}

	/* 병합 이후 페이지 이동과 출력을 위한 함수 목록 */

//	@GetMapping("/content")
//	@ResponseBody
//	public Map<String, Object> contentGET(@RequestParam("sidoCode") Integer sidoCode, 
//	                                      @RequestParam("gugunCode") Integer gugunCode, 
//	                                      @RequestParam("contentTypeId") Integer contentTypeId) throws ServletException, IOException {
//		System.out.println("attraction - contentGET 실행");
//		try {
//			// 여행지 목록 가져오기
//			List<AttractionDto> attractionList = attractionService.attractionList(sidoCode, gugunCode, contentTypeId);
//			int size = attractionList.size();
//			Collections.sort(attractionList, (o1, o2) -> {
//				return Double.compare(o1.getLatitude() + o1.getLongitude(), o2.getLatitude() + o2.getLongitude());
//			});
//			
//			size = Math.min(size, 5);
//			List<AttractionDto> answerList = new ArrayList<>();
//			
//			if (size != 0) {
//				double[][] dp = new double[size][(1 << size) - 1];
//				double[][] distance = new double[size][size];
//				int[][] nextCity = new int[size][(1 << size) - 1];
//				
//				for (int i = 0; i < size; i++) {
//					Arrays.fill(dp[i], -1);
//					Arrays.fill(nextCity[i], -1);
//				}
//
//				for (int r = 0; r < size; r++) {
//					double mainLAT = attractionList.get(r).getLatitude();
//					double mainLON = attractionList.get(r).getLongitude();
//
//					for (int c = 0; c < size; c++) {
//						if (r == c) continue;
//						double subLAT = attractionList.get(c).getLatitude();
//						double subLON = attractionList.get(c).getLongitude();
//
//						double dist = Math.abs(mainLAT - subLAT) + Math.abs(mainLON - subLON);
//						distance[r][c] = dist * 100; // 거리 초기화 
//					}
//				}
//				System.out.println("result: " + tsp(0, 1, size, dp, distance, nextCity));
//				
//				int visited = 1;
//				int current = 0;
//				answerList.add(attractionList.get(current));
//				// 해당 노드의 방문 여부를 기록하기 위해 while문을 돌면서 비트마스킹으로 체킹
//				while (visited != (1 << size) - 1) {
//			        current = nextCity[current][visited];
//					answerList.add(attractionList.get(current));
//			        visited |= (1 << current);
//			    }
//				
//			}
//			// 추가할 값이 있으면 아래와 같은 방식으로 put해서 key value 방식으로 전달해주면 됨
//			Map<String, Object> dataMap = new HashMap<>();
//			dataMap.put("attractions", attractionList);
//			dataMap.put("answers", answerList);
//			
//			System.out.println("여행지 받음");
//			
//			return dataMap;
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	private static final int INF = 1000000000;
//
//	private double tsp(int current, int visited, int size, double[][] dp, double[][] distance, int[][] nextCity) {
//	    if (visited == (1 << size) - 1) {
//	        return distance[current][0] == 0 ? INF : distance[current][0];
//	    }
//
//	    if (dp[current][visited] != -1) {
//	        return dp[current][visited];
//	    }
//
//	    dp[current][visited] = INF;
//
//	    for (int next = 0; next < size; next++) {
//	        if ((visited & (1 << next)) != 0 || distance[current][next] == 0) continue;
//
//	        double cost = tsp(next, visited | (1 << next), size, dp, distance, nextCity) + distance[current][next];
//	        if (dp[current][visited] > cost) {
//	            dp[current][visited] = cost;
//	            nextCity[current][visited] = next;
//	        }
//	    }
//	    return dp[current][visited];
//	}
}
