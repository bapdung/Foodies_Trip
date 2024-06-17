package com.foodiestrip.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanChecklistDto {
	private int planChecklistNo;
    private String userId;
    private int planNo;
    private int checkNo;
    private boolean isCheck;
    private String checkItem;
    
 // Getter and Setter for isCheck
    public boolean getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }
}
