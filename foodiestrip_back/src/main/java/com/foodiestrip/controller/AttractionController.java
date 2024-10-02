package com.foodiestrip.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class AttractionController {
	private final AttractionService attractionService;

	@GetMapping("/view")
	public ResponseEntity<?> viewGet() {
		log.info("view attraction");
		System.out.println("view attraction");
		try {
			List<SidoDto> sidoList = attractionService.sidoList();
			List<GugunDto> gugunList = attractionService.gugunList(1);
			Map<String, Object> response = new HashMap<>();
			response.put("sidoList", sidoList);
			response.put("gugunList", gugunList);
			// log.info("시도 API 결과가 호출: {}",response);
			return ResponseEntity.ok(response);
		} catch (SQLException e) {
			return ResponseEntity.internalServerError().body("데이터베이스 오류가 발생했습니다.");
		}
	}

	//loginjoin 페이지에서 api로 안 받아줘서 RequestParam으로 받아야하는 문제 발생
	//loginJoinView 수정 후 밑 함수만 남기고 지워야할듯
	@GetMapping("/gugun")
	public ResponseEntity<?> gugunGetQuery(@RequestParam(value = "sidoCode", required = true) String sidoCode) {
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
}
