package com.foodiestrip.model.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.foodiestrip.model.dao.AttractionDao;
import com.foodiestrip.model.dto.AttractionDto;
import com.foodiestrip.model.dto.GugunDto;
import com.foodiestrip.model.dto.SidoDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttractionServiceImpl implements AttractionService {
	private final AttractionDao attractionDao;

	@Override
	public List<SidoDto> sidoList() throws SQLException {

		return attractionDao.sidoList();
	}

	@Override
	public List<GugunDto> gugunList(int sidoCode) throws SQLException {

		return attractionDao.gugunList(sidoCode);
	}

	@Override
	public List<AttractionDto> attractionList(AttractionDto attractionDto) throws SQLException {
		return attractionDao.attractionList(attractionDto);
	}

	@Override
	public List<Integer> countDate(int[] sido_code) throws SQLException {
		return attractionDao.countDate(sido_code);
	}

	@Override
	public List<AttractionDto> recommendList(int sido_code) throws SQLException {
		return attractionDao.recommendList(sido_code);
	}

	@Override
	public AttractionDto getAttraction(int contentId) throws SQLException {
		return attractionDao.getAttraction(contentId);
	}

}
