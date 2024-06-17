package com.foodiestrip.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GugunDto {
	private int gugunCode;
	private String gugunName;
	private int sidoCode;

}
