package com.ninjamap.app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "article_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleStats {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Builder.Default
	private Integer views = 0;

	@Builder.Default
	private Integer likes = 0;

	@Builder.Default
	private Integer comments = 0;

	@Builder.Default
	private Integer shares = 0;
}
