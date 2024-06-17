package com.foodiestrip.model.dto;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KMPDto {
	private String spam;
	private int spamLen;
	private int pi[];
}
