package com.ninjamap.app.payload.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchHistoryListResponse {

	private List<SearchHistoryResponse> searchHistories;
	private Integer totalCount;
	private Integer pageSize;
	private Integer pageNumber;
}
