//package com.ninjamap.app.model;
//
//import java.time.LocalDateTime;
//
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.annotation.LastModifiedDate;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.EntityListeners;
//import jakarta.persistence.MappedSuperclass;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.experimental.SuperBuilder;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@MappedSuperclass
//@EntityListeners(AuditingEntityListener.class)
//@SuperBuilder
//public abstract class AuditData {
//
//	/**
//	 * The date and time when the entity was created. Managed automatically by
//	 * Spring Data JPA.
//	 */
//	@CreatedDate
//	@Column(nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
//	private LocalDateTime createdDate;
//
//	/**
//	 * The date and time when the entity was last updated. Managed automatically by
//	 * Spring Data JPA.
//	 */
//	@LastModifiedDate
//	@Column(nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
//	private LocalDateTime updatedDate;
//
//	/**
//	 * Flag indicating whether the entity is deleted. Defaults to {@code false}.
//	 */
//	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
//	@Builder.Default
//	private Boolean isDeleted = Boolean.FALSE;
//
//	/**
//	 * Flag indicating whether the entity is active. Defaults to {@code true}.
//	 */
//	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
//	@Builder.Default
//	private Boolean isActive = Boolean.TRUE;
//
//}





package com.ninjamap.app.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder
public abstract class AuditData {

    /**
     * The date and time when the entity was created. Managed automatically by
     * Spring Data JPA.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    /**
     * The date and time when the entity was last updated. Managed automatically by
     * Spring Data JPA.
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedDate;

    /**
     * Flag indicating whether the entity is deleted. Defaults to {@code false}.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = Boolean.FALSE;

    /**
     * Flag indicating whether the entity is active. Defaults to {@code true}.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = Boolean.TRUE;
}
