package com.ninjamap.app.model;

import com.ninjamap.app.enums.ReportSeverity;
import com.ninjamap.app.enums.ReportStatus;
import com.ninjamap.app.enums.ReportType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class Report extends AuditData {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReportType reportType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReportStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReportSeverity severity;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String comment;

	@Column(nullable = false)
	private Double latitude;

	@Column(nullable = false)
	private Double longitude;

	private String location;

	@Column(length = 500)
	private String address;

	@Column(nullable = false)
	private String userId;

	@Column(nullable = false)
	private Integer viewCount;

	@Column(nullable = false)
	private Integer helpfulCount;

	@Column(nullable = false)
	private Integer notHelpfulCount;

	private Boolean hideName;
	
	private String reportPicture;

	@Column
	private java.time.LocalDateTime resolvedAt;
}
