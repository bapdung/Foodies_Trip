package com.foodiestrip.controller;

import java.util.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodiestrip.model.dao.KoGPTService;
import com.foodiestrip.model.dto.FoodBoardDto;
import com.foodiestrip.model.service.FoodBoardService;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class KoGPTController {

	private final KoGPTService gptService;
	private final FoodBoardService foodBoardService;

	StringBuilder prompt;

	@PostMapping("/generate")
	public ResponseEntity<?> generateText(@RequestBody String contentId) {
		prompt= new StringBuilder();
		try {
			int contentid = Integer.parseInt(contentId);
			List<FoodBoardDto> foodBoard = foodBoardService.getStoreReviews(contentid);
			for (FoodBoardDto boardReview : foodBoard) {
				prompt.append(boardReview.getFoodBoardReview().replaceAll("[~!@%*_+.$^&#]", " ")).append(" ");
			}
			prompt.append("한줄 요약:");
			// log.info("prompt String: {}",prompt);

			KoGPTService.ResponseBody responseBody = gptService.generateText(prompt.toString());
			log.info("summary result : {}", responseBody.getGenerations());
			
			return ResponseEntity.ok(responseBody);
		} catch (Exception e) {
			log.error("Error occurred: {}", e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/generatebyPrompt")
    public ResponseEntity<?> generateTextfilter(@RequestBody String promptString) {
		prompt= new StringBuilder();
		prompt.append(promptString.replaceAll("[~!@%*_+.$^&#\"\']", "")).append(" ").append("한줄 요약:");
		
		log.info("prompt: {}",prompt);
        try {
            KoGPTService.ResponseBody responseBody = gptService.generateText(prompt.toString());     
            log.info("response : {}",responseBody.getGenerations());
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            log.error("Error occurred: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
	
}
