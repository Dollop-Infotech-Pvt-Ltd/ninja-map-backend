package com.ninjamap.app.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.context.properties.bind.DefaultValue;

import com.ninjamap.app.enums.StoryCategory;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "customer_story")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class CustomerStory extends AuditData {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StoryCategory category;

	private Double rating=0.0;

	@Column(columnDefinition = "TEXT")
	private String authorBio;
	
	@Column(columnDefinition = "TEXT")
	private String organisationName;

	@Column(columnDefinition = "TEXT")
	private String authorProfilePicture;
	
	@Column(columnDefinition = "TEXT")
	private String authorEmail;
	
	@Column(columnDefinition = "TEXT")
	private String authorName;

	@Column(columnDefinition = "TEXT")
	private String location;
	
	 @Builder.Default
	private Boolean isApproved=false;

	@Embedded
	@Builder.Default
	private ArticleStats stats = new ArticleStats();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "customer_story_likes", joinColumns = @JoinColumn(name = "story_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	@Builder.Default
	private Set<User> likedByUsers = new HashSet<>();


}
