package com.foodiestrip.util;

import com.foodiestrip.model.dto.Criteria;

public class PageMaker {
	private Criteria cri; // 기준점 객체
	private int totalRow; // 데이터 전체 개수
	private int pageCount = 10; // 보여질 페이지 개수
	private int startPage; // 보여지는 페이지의 시작번호
	private int endPage; // 보여지는 페이지의 끝번호
	private boolean prev; // 이전 페이지 존재 여부
	private boolean next; // 다음 페이지 존재 여부

	public Criteria getCri() {
		return cri;
	}

	public void setCri(Criteria cri) {
		this.cri = cri;
	}

	public int getTotalRow() {
		return totalRow;
	}

	public void setTotalRow(int totalRow) {
		this.totalRow = totalRow;
		
		cal();
	}

	private void cal() {
		endPage = (int)(Math.ceil(cri.getCurPage() / (double)pageCount) * pageCount);
		
		startPage = endPage - pageCount + 1;
		if (startPage <= 0) startPage = 1;
		
		int tempEndPage = (int)(Math.ceil(totalRow / (double)cri.getRowCount()));
		if (endPage > tempEndPage) endPage = tempEndPage;
		
		prev = startPage == 1 ? false : true;
		next = endPage * cri.getRowCount() >= totalRow ? false : true;
	}
	
	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}

	public boolean isPrev() {
		return prev;
	}

	public void setPrev(boolean prev) {
		this.prev = prev;
	}

	public boolean isNext() {
		return next;
	}

	public void setNext(boolean next) {
		this.next = next;
	}

}
