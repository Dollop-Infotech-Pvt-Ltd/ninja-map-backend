package com.ninjamap.app.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ninjamap.app.enums.BlogCategory;

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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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

	@Column(columnDefinition = "TEXT", nullable = false)
	private String previewContent; // shown in list view

	@Column(columnDefinition = "LONGTEXT", nullable = false)
	private String detailedContent; // shown in "Learn More"

	@Column(nullable = false)
	private BlogCategory category;

	@Column(name = "thumbnail_url", columnDefinition = "TEXT")
	private String thumbnailUrl; // single preview image

	private String featuredImageUrl;

	private Boolean isFeaturedArticle;

	@Builder.Default
	private Integer readTimeMinutes = 0;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_author_id")
	private User userAuthor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_author_id")
	private Admin adminAuthor;

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

	// Many-to-Many relationships for user engagement
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "blog_post_likes", joinColumns = @JoinColumn(name = "blog_post_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	@Builder.Default
	private Set<User> likedByUsers = new HashSet<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "blog_post_saves", joinColumns = @JoinColumn(name = "blog_post_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	@Builder.Default
	private Set<User> savedByUsers = new HashSet<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "blog_post_shares", joinColumns = @JoinColumn(name = "blog_post_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	@Builder.Default
	private Set<User> sharedByUsers = new HashSet<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "blog_post_views", joinColumns = @JoinColumn(name = "blog_post_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	@Builder.Default
	private Set<User> viewedByUsers = new HashSet<>();

	// ===== Helper Methods for Author Info =====
	@Transient
	public String getAuthorName() {
		if (userAuthor != null)
			return userAuthor.getFirstName() + " " + userAuthor.getLastName();
		if (adminAuthor != null)
			return adminAuthor.getFirstName() + " " + adminAuthor.getLastName();
		return "Unknown Author";
	}

	@Transient
	public String getAuthorRole() {
		if (userAuthor != null)
			return userAuthor.getRole().getRoleName();
		if (adminAuthor != null)
			return adminAuthor.getRole().getRoleName();
		return "UNKNOWN";
	}

	@Transient
	public String getAuthorBio() {
		if (userAuthor != null)
			return userAuthor.getBio();
		if (adminAuthor != null)
			return adminAuthor.getBio();
		return "";
	}

	@Transient
	public String getAuthorProfile() {
		if (userAuthor != null)
			return userAuthor.getProfilePicture();
		if (adminAuthor != null)
			return adminAuthor.getProfilePicture();
		return "";
	}

}
