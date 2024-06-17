package com.foodiestrip.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface SpamDao {
	public List<String> spamList(); 
}
