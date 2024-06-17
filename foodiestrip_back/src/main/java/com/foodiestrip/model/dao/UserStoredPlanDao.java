package com.foodiestrip.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.foodiestrip.model.dto.UserStoredPlanDto;

@Mapper
public interface UserStoredPlanDao {

    // 사용자 저장된 계획 삽입
    void insertUserStoredPlan(UserStoredPlanDto userStoredPlanDto);

    // 사용자 저장된 계획 삭제
    void deleteUserStoredPlan(UserStoredPlanDto userStoredPlanNo);

    // 특정 사용자의 모든 저장된 계획 조회
    List<UserStoredPlanDto> selectAllUserStoredPlans(String userId);
}
