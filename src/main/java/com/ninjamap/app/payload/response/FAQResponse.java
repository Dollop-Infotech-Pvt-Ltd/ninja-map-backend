package com.ninjamap.app.payload.response;

import java.util.List;

import com.ninjamap.app.payload.request.QuestionAnswerDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FAQResponse {
	private String id;
	private String category;
	private String categoryImageUrl;
	private List<QuestionAnswerDTO> questions;
}
