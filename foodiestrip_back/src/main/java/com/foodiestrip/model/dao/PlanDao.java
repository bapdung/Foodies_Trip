package com.foodiestrip.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.foodiestrip.model.dto.PlanChecklistDto;
import com.foodiestrip.model.dto.PlanDetailDto;
import com.foodiestrip.model.dto.PlanDto;
@Mapper
public interface PlanDao {

    // 계획을 삽입하고 자동 생성된 키를 가져옴
    void insertPlan(PlanDto planDto);

    // 계획의 세부사항을 삽입
    void insertPlanDetail(PlanDetailDto planDetailDto);

    // 특정 계획 조회
    PlanDto selectPlanByPlanNo(int planNo);

    // 특정 계획의 세부사항 조회
    List<PlanDetailDto> selectPlanDetailByPlanNo(int planNo);

    // 계획 조회수 증가
    void updatePlanHit(int planNo);

    // 계획 찜수 증가
    void updatePlanJjim(int planNo);
    
    //계획 찜수 감소
    void updatePlanJjimMinus(int planNo);

    // 특정 사용자의 모든 계획 조회
    List<PlanDto> selectUserPlans(String userId);
    
    //planBoard에 올라간 게시글 조회
    List<PlanDto> selectAllSharedPlans();
    
    //planBoard filtering
    List<PlanDto> searchPlanList(PlanDto planDto);
    
    //특정 계획의 plan_no 조회
    int selectPlanNo(PlanDto planDto);

    // 모든 계획 조회
    List<PlanDto> selectAllPlans();
    
    //계획 정렬 조회
    List<PlanDto> selectAllSharedPlansByOrderAsc(String col);
    
    //계획 정렬 조회 (내림차순)
    List<PlanDto> selectAllSharedPlansByOrderDesc(String col);
    
    //체크리스트 선택
    List<PlanChecklistDto> selectPlanChecklist(int planNo);
    
    //랜덤추출
    List<PlanDto> selectRecommend(PlanDto planDto);

    //체크리스트 삭제
    int deletePlanChecklist(int planNo);
    
    //체크리스트 저장
    int insertPlanChecklist(PlanChecklistDto checklistDto);
    
    void deletePlanByPlanNo(int planNo);
    void deletePlanDetailByPlanNo(int planNo);
}
