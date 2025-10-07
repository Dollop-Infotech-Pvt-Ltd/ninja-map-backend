package com.ninjamap.app.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ninjamap.app.enums.BlogCategory;
import com.ninjamap.app.utils.annotations.UUIDValidator;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "blog_post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class BlogPost extends AuditData {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(nullable = false)
	private BlogCategory category;

	private String featuredImageUrl;

	@Builder.Default
	private Integer readTimeMinutes = 0;

	@UUIDValidator(message = ValidationConstants.INVALID_UUID)
	@Column(nullable = false)
	private String authorId;

	@Column(nullable = false)
	private String authorRole;

	@ElementCollection
	@CollectionTable(name = "blog_post_tags", joinColumns = @JoinColumn(name = "blog_post_id"))
	@Column(name = "tag")
	@Builder.Default
	private Set<String> tags = new HashSet<>();

	@OneToMany(mappedBy = "blogPost", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Comment> comments = List.of();

	// One-to-one with stats
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "stats_id", referencedColumnName = "id")
	private ArticleStats stats;
}
