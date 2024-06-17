package com.foodiestrip.model.service;

import java.sql.SQLException;
import java.util.*;

import com.foodiestrip.model.dto.AttractionDto;
import com.foodiestrip.model.dto.GugunDto;
import com.foodiestrip.model.dto.SidoDto;

public interface AttractionService {
	public List<SidoDto> sidoList() throws SQLException ;
	public List<GugunDto> gugunList(int sidoCode) throws SQLException ;
	public List<AttractionDto> attractionList(AttractionDto attractionDto) throws SQLException;
	public List<Integer> countDate(int[] sido_code) throws SQLException ;
	public List<AttractionDto> recommendList(int sido_code) throws SQLException;
	AttractionDto getAttraction(int contentId) throws SQLException;

}
