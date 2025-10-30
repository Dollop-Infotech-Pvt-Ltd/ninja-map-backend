package com.ninjamap.app.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleStats {

	@Builder.Default
	private Integer views = 0;

	@Builder.Default
	private Integer likes = 0;

	@Builder.Default
	private Integer comments = 0;

	@Builder.Default
	private Integer shares = 0;

	@Builder.Default
	private Integer shaved = 0;
}
