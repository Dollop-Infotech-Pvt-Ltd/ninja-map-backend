package com.ninjamap.app.payload.response;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponse<T> {

	private List<T> content;
	private int pageNumber;
	private int pageSize;
	private long totalElements;
	private int totalPages;
	private int numberOfElements;
	private boolean firstPage;
	private boolean lastPage;

	public PaginatedResponse(Page<T> page) {
		this.content = page.getContent();
		this.pageNumber = page.getNumber();
		this.pageSize = page.getSize();
		this.totalElements = page.getTotalElements();
		this.totalPages = page.getTotalPages();
		this.numberOfElements = page.getNumberOfElements();
		this.firstPage = page.isFirst();
		this.lastPage = page.isLast();
	}
}
