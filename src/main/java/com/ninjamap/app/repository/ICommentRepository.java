package com.ninjamap.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.model.Comment;

@Repository
public interface ICommentRepository extends JpaRepository<Comment, String> {
	List<Comment> findByBlogPostId(String blogPostId);

	Page<Comment> findByBlogPostId(String blogPostId, Pageable pageable);

	// Fetch only active comments (isDeleted = false) for a blog post, latest first
	List<Comment> findByBlogPostIdAndIsDeletedFalseOrderByCreatedDateDesc(String blogPostId);

	Optional<Comment> findByIdAndIsDeletedFalse(String id);

}
