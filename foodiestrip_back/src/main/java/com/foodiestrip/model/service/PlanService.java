package com.foodiestrip.model.service;

import java.util.List;

import com.foodiestrip.model.dto.PlanDto;
import com.foodiestrip.model.dto.UserStoredPlanDto;


public interface PlanService {
	int insert(PlanDto plandto);
	List<PlanDto> selectPlanList();
	void storedPlan(UserStoredPlanDto userStoredPlanDto) throws Exception;
	void deleteStoredPlan(UserStoredPlanDto userStoredPlanDto) throws Exception;
	void deleteUserPlan(int planNo) throws Exception;
	void updatePlanHit(int planNo) throws Exception;
	List<PlanDto> selectAllSharedPlansByOrder(String col) throws Exception;
	List<PlanDto> getUserPlan(String userId) throws Exception;
	PlanDto getUserPlanByPlanNo(int planNo) throws Exception;
	List<PlanDto> getUserStoredPlan(String userId) throws Exception;
	List<PlanDto> getRandomPlan(PlanDto planDto) throws Exception;
	List<PlanDto> searchPlanList(PlanDto planDto) throws Exception;
}
