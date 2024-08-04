package com.foodiestrip.model.dao;

import com.foodiestrip.model.dto.RefreshTokenDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefreshTokenDao {
    RefreshTokenDto findById(Long id);
    RefreshTokenDto findByUserId(Long userId);
    RefreshTokenDto findByRefreshToken(String refreshToken);
    void insert(RefreshTokenDto refreshTokenDto);
    void update(Long userId, String refreshToken);
    void delete(Long id);
}
