package com.foodiestrip.model.dto;

public class Criteria {
	private int curPage; // 현재 페이지
	private int rowCount; // 한 페이지에서 보여질 데이터 개수
	
	public Criteria() {
		this.curPage = 1;
		this.rowCount = 10;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		// 사실상 10으로 고정시킴
//		this.rowCount = rowCount;
	}
	
}
