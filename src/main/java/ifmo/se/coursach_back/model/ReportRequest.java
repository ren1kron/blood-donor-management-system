package ifmo.se.coursach_back.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "report_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "donor_id", nullable = false)
    private DonorProfile donor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "requested_by_staff_id", nullable = false)
    private StaffProfile requestedBy;

    @Column(name = "requested_by_role")
    private String requestedByRole;

    @ManyToOne
    @JoinColumn(name = "assigned_admin_id")
    private StaffProfile assignedAdmin;

    @Column(name = "report_type", nullable = false)
    private String reportType;

    @Column(nullable = false)
    private String status;

    @Column(name = "payload_json", columnDefinition = "jsonb")
    private String payloadJson;

    @Column(name = "generated_at")
    private OffsetDateTime generatedAt;

    @Column(name = "message")
    private String message;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (status == null) {
            status = ReportRequestStatus.REQUESTED;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
