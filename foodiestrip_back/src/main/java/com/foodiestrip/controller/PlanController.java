package com.foodiestrip.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodiestrip.model.dto.PlanDto;
import com.foodiestrip.model.dto.UserStoredPlanDto;
import com.foodiestrip.model.service.PlanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/plan")
@RequiredArgsConstructor
@Tag(name = "플랜 컨트롤러", description = "플랜 관련 처리하는 클래스.")
@Slf4j
public class PlanController {
	
	private final PlanService planService;
	
	@Operation(summary="사용자 플랜 추가")
	@PostMapping("/insert")
	public ResponseEntity<Map<String, Object>> insertPlan(@RequestBody @Parameter(required =true) PlanDto planDto){
		log.debug("plan - insertPOST 실행");
		System.out.println(planDto.getPlanChecklistDto());
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HttpStatus status = HttpStatus.ACCEPTED;
		int planNo = planService.insert(planDto);
		resultMap.put("planNo", planNo);
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}
	
	@Operation(summary="사용자 플랜보드 리스트")
	@GetMapping("/list")
	public ResponseEntity<Map<String, Object>>selectPlanList(){
		log.debug("plan - selectPlanListGET 실행");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HttpStatus status = HttpStatus.ACCEPTED;
		List<PlanDto> planList = planService.selectPlanList();
		resultMap.put("planList", planList);
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}
	
	@Operation(summary="사용자 플랜보드 리스트 필터링")
	@PostMapping("/search")
	public ResponseEntity<Map<String, Object>>searchPlanList(@RequestBody @Parameter(required =true) PlanDto planDto){
		log.debug("plan - searchPlanListPost 실행");
		System.out.println(planDto);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HttpStatus status = HttpStatus.ACCEPTED;
		List<PlanDto> planList = null;
		try {
			planList = planService.searchPlanList(planDto);
		} catch (Exception e) {
			log.debug("사용자 플랜보드 리스트 필터링 에러 발생 : {}", e);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		resultMap.put("planList", planList);
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}
	
	@Operation(summary="사용자 플랜보드 리스트 (정렬)")
	@GetMapping("/order/{col}")
	public ResponseEntity<Map<String, Object>>selectPlanList(@PathVariable(name = "col", required = true) String col, HttpServletRequest request){
		log.debug("plan - selectPlanList(정렬)POST 실행");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HttpStatus status = HttpStatus.ACCEPTED;
		List<PlanDto> planList = null;
		try {
			planList = planService.selectAllSharedPlansByOrder(col);
			resultMap.put("planList", planList);
		} catch (Exception e) {
			log.debug("사용자 플랜보드 리스트 (정렬) 에러 발생 : {}", e);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		System.out.println(planList);
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}
	
	@Operation(summary="플랜 조회수 증가")
	@GetMapping("/hit/{planNo}")
	public ResponseEntity<Map<String, Object>>updatePlanHit(@PathVariable(name = "planNo", required = true) int planNo, HttpServletRequest request){
		log.debug("plan - updatePlanHitGET 실행");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HttpStatus status = HttpStatus.ACCEPTED;
		try {
			planService.updatePlanHit(planNo);
		} catch (Exception e) {
			log.debug("플랜 조회수 증가 에러 발생 : {}", e);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}
	
	@Operation(summary="플랜 스크랩(저장)")
	@PostMapping("/store")
	public ResponseEntity<Map<String, Object>> storedPlan(@RequestBody @Parameter(required=true) UserStoredPlanDto plan){
		log.debug("plan - storedPlanPOST 실행");
		Map<String,Object> resultMap = new HashMap<String, Object>();
		HttpStatus status = HttpStatus.ACCEPTED;
		try {
			planService.storedPlan(plan);
			resultMap.put("color", "yellow"); //별 색깔
		} catch (Exception e) {
			log.debug("플랜 스크랩(저장) 에러 발생 : {}", e);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
		
	}
	
	@Operation(summary="플랜 스크랩 삭제")
	@PostMapping("/deleteStore")
	public ResponseEntity<Map<String, Object>> deleteStoredPlan(@RequestBody @Parameter(required=true) UserStoredPlanDto plan){
		log.debug("plan - deleteStoredPlanPOST 실행");
		Map<String,Object> resultMap = new HashMap<String, Object>();
		HttpStatus status = HttpStatus.ACCEPTED;
		try {
			planService.deleteStoredPlan(plan);
			resultMap.put("color", "gray"); //별 색깔
		} catch (Exception e) {
			log.debug("플랜 스크랩 취소 에러 발생 : {}", e);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}
	
	@Operation(summary="사용자 플랜 조회 ")
	@GetMapping("/user/{userId}")
	public ResponseEntity<Map<String, Object>>getUserPlan(@PathVariable(name = "userId", required = true) String userId, HttpServletRequest request){
		log.debug("plan - getUserPlanGET 실행");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HttpStatus status = HttpStatus.ACCEPTED;
		List<PlanDto> planList = null;
		try {
			planList = planService.getUserPlan(userId);
			resultMap.put("planList", planList);
		} catch (Exception e) {
			log.debug("사용자 플랜 조회 에러 발생 : {}", e);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}
	
	@Operation(summary="사용자 특정 플랜 조회(planNo)이용 ")
	@GetMapping("/user/planNo/{planNo}")
	public ResponseEntity<Map<String, Object>>getUserPlanByPlanNo(@PathVariable(name = "planNo", required = true) int planNo, HttpServletRequest request){
		log.debug("plan - getUserPlanByPlanNoGET 실행");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HttpStatus status = HttpStatus.ACCEPTED;
		PlanDto planDto = null;
		try {
			planDto = planService.getUserPlanByPlanNo(planNo);
			resultMap.put("plan", planDto);
		} catch (Exception e) {
			log.debug("사용자 특정 플랜 조회(planNo)이용 중 에러 발생 : {}", e);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		System.out.println(resultMap);
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}
	
	@Operation(summary="사용자가 저장한 플랜 조회 ")
	@GetMapping("/stored/{userId}")
	public ResponseEntity<Map<String, Object>>getUserStoredPlan(@PathVariable(name = "userId", required = true) String userId, HttpServletRequest request){
		log.debug("plan - getUserStoredPlanGET 실행");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HttpStatus status = HttpStatus.ACCEPTED;
		List<PlanDto> planList = null;
		try {
			planList = planService.getUserStoredPlan(userId);
			resultMap.put("planList", planList);
		} catch (Exception e) {
			log.debug("사용자 플랜 조회 에러 발생 : {}", e);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}
	
	@Operation(summary="플랜 랜덤추출")
	@PostMapping("/random")
	public ResponseEntity<Map<String, Object>> getRandomPlan(@RequestBody @Parameter(required=true) PlanDto planDto){
		log.debug("plan - getRandomPlanPOST 실행");
		Map<String,Object> resultMap = new HashMap<String, Object>();
		HttpStatus status = HttpStatus.ACCEPTED;
		List<PlanDto> planList = null;
		try {
			planList = planService.getRandomPlan(planDto);
			resultMap.put("planList", planList);
		} catch (Exception e) {
			log.debug("사용자 플랜보드 리스트 (정렬) 에러 발생 : {}", e);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}
	
	@Operation(summary="사용자가 저장한 플랜 삭제 ")
	@GetMapping("/delete/{planNo}")
	public ResponseEntity<Map<String, Object>>deleteUserPlan(@PathVariable(name = "planNo", required = true) int planNo, HttpServletRequest request){
		log.debug("plan - deleteUserPlanGET 실행");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HttpStatus status = HttpStatus.ACCEPTED;
		try {
			planService.deleteUserPlan(planNo);
		} catch (Exception e) {
			log.debug("사용자 플랜 삭제 중 에러 발생 : {}", e);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}
	
}
