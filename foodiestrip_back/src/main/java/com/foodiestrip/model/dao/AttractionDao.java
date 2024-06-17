package com.foodiestrip.model.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.foodiestrip.model.dto.AttractionDto;
import com.foodiestrip.model.dto.GugunDto;
import com.foodiestrip.model.dto.SidoDto;

@Mapper
public interface AttractionDao {
	public List<SidoDto> sidoList() throws SQLException;
	public List<GugunDto> gugunList(int sidoCode) throws SQLException;
	public List<AttractionDto> attractionList(AttractionDto attractionDto) throws SQLException;
	public List<Integer> countDate(int[] sido_code) throws SQLException;
	public List<AttractionDto> recommendList(int sido_code) throws SQLException;
	public AttractionDto getAttraction(int content_id) throws SQLException;
}

