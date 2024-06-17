package com.foodiestrip.model.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.foodiestrip.model.dao.PlanDao;
import com.foodiestrip.model.dao.UserStoredPlanDao;
import com.foodiestrip.model.dto.PlanChecklistDto;
import com.foodiestrip.model.dto.PlanDetailDto;
import com.foodiestrip.model.dto.PlanDto;
import com.foodiestrip.model.dto.UserStoredPlanDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

	private final PlanDao planDao;
	private final UserStoredPlanDao userStoredPlanDao;

	@Override
	public int insert(PlanDto planDto) {
		planDao.insertPlan(planDto);
		int planNo = planDao.selectPlanNo(planDto);// 제목, 아이디 기반으로 가져오기
		List<PlanDetailDto> planDetailList = planDto.getPlanDetailDto();
		List<PlanChecklistDto> planCheckList = planDto.getPlanChecklistDto();
		if (planDetailList != null) {
			for (int i = 0; i < planDetailList.size(); i++) {
				planDetailList.get(i).setPlanNo(planNo);
				planDao.insertPlanDetail(planDetailList.get(i));
			}
		}
		if (planCheckList != null) {
			for (int i = 0; i < planCheckList.size(); i++) {
				planCheckList.get(i).setPlanNo(planNo);
				planDao.insertPlanChecklist(planCheckList.get(i));
			}
		}

		System.out.println(planCheckList);
		return planNo;
	}

	@Override
	public List<PlanDto> selectPlanList() {
		List<PlanDto> planList = planDao.selectAllSharedPlans();
		for (PlanDto plan : planList) {
			int planNo = plan.getPlanNo();
			List<PlanDetailDto> detailPlan = planDao.selectPlanDetailByPlanNo(planNo);
			plan.setPlanDetailDto(detailPlan);
		}
		return planList;
	}

	@Override
	public List<PlanDto> searchPlanList(PlanDto planDto) throws Exception {
		List<PlanDto> planList = planDao.searchPlanList(planDto);
		for (PlanDto plan : planList) {
			int planNo = plan.getPlanNo();
			List<PlanDetailDto> detailPlan = planDao.selectPlanDetailByPlanNo(planNo);
			plan.setPlanDetailDto(detailPlan);
		}
		return planList;
	}

	@Override
	public void storedPlan(UserStoredPlanDto userStoredPlanDto) throws Exception {
		userStoredPlanDao.insertUserStoredPlan(userStoredPlanDto);
		planDao.updatePlanJjim(userStoredPlanDto.getPlanNo());

	}

	@Override
	public void deleteStoredPlan(UserStoredPlanDto userStoredPlanDto) throws Exception {
		userStoredPlanDao.deleteUserStoredPlan(userStoredPlanDto);
		planDao.updatePlanJjimMinus(userStoredPlanDto.getPlanNo());

	}

	@Override
	public void updatePlanHit(int planNo) throws Exception {
		planDao.updatePlanHit(planNo);
	}

	@Override
	public List<PlanDto> selectAllSharedPlansByOrder(String col) throws Exception {
		List<PlanDto> planList = null;
		if (col.equals("오래된 속성")) {
			// 아직 안 함
		} else {
			planList = planDao.selectAllSharedPlansByOrderDesc(col);
			for (PlanDto plan : planList) {
				int planNo = plan.getPlanNo();
				List<PlanDetailDto> detailPlan = planDao.selectPlanDetailByPlanNo(planNo);
				plan.setPlanDetailDto(detailPlan);
			}
		}
		return planList;

	}

	@Override
	public List<PlanDto> getUserPlan(String userId) throws Exception {
		List<PlanDto> planList = null;
		planList = planDao.selectUserPlans(userId);
		for (PlanDto plan : planList) {
			int planNo = plan.getPlanNo();
			List<PlanDetailDto> detailPlan = planDao.selectPlanDetailByPlanNo(planNo);
			plan.setPlanDetailDto(detailPlan);
		}
		return planList;
	}

	@Override
	public PlanDto getUserPlanByPlanNo(int planNo) throws Exception {
		PlanDto planDto = planDao.selectPlanByPlanNo(planNo);
		System.out.println();
		planDto.setPlanDetailDto(planDao.selectPlanDetailByPlanNo(planNo));
		planDto.setPlanChecklistDto(planDao.selectPlanChecklist(planNo));
		return planDto;
	}

	@Override
	public List<PlanDto> getUserStoredPlan(String userId) throws Exception {
		List<UserStoredPlanDto> planList = null;
		List<PlanDto> rst = new ArrayList<>();
		planList = userStoredPlanDao.selectAllUserStoredPlans(userId);
		for (UserStoredPlanDto plan : planList) {
			int planNo = plan.getPlanNo();
			PlanDto planDto = planDao.selectPlanByPlanNo(planNo);
			planDto.setPlanDetailDto(planDao.selectPlanDetailByPlanNo(planNo));
			rst.add(planDto);
		}
		return rst;
	}

	@Override
	public List<PlanDto> getRandomPlan(PlanDto planDto) throws Exception {
		List<PlanDto> planList = null;
		planList = planDao.selectRecommend(planDto);
		for (PlanDto plan : planList) {
			int planNo = plan.getPlanNo();
			List<PlanDetailDto> detailPlan = planDao.selectPlanDetailByPlanNo(planNo);
			plan.setPlanDetailDto(detailPlan);
		}
		List<PlanDto> subList = planList.subList(0, Math.min(planList.size(), 5));
		return subList;
	}

	@Override
	public void deleteUserPlan(int planNo) throws Exception {
		planDao.deletePlanChecklist(planNo);
		planDao.deletePlanDetailByPlanNo(planNo);
		planDao.deletePlanByPlanNo(planNo);
		
	}

}
