package ifmo.se.coursach_back.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "medical_check")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "visit_id", nullable = false, unique = true)
    private Visit visit;

    // Lab technician who filled the form
    @ManyToOne
    @JoinColumn(name = "submitted_by_lab_id")
    private StaffProfile submittedByLab;
    
    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;

    // Doctor who reviewed and made decision
    @ManyToOne
    @JoinColumn(name = "performed_by_staff_id")
    private StaffProfile performedBy;

    @Column(name = "weight_kg")
    private BigDecimal weightKg;

    @Column(name = "hemoglobin_g_l")
    private BigDecimal hemoglobinGl;

    @Column(name = "systolic_mmhg")
    private Integer systolicMmhg;

    @Column(name = "diastolic_mmhg")
    private Integer diastolicMmhg;
    
    @Column(name = "pulse_rate")
    private Integer pulseRate;
    
    @Column(name = "body_temperature_c")
    private BigDecimal bodyTemperatureC;

    // Status: PENDING_REVIEW, ADMITTED, REFUSED
    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String decision;

    @Column(name = "decision_at", nullable = false)
    private OffsetDateTime decisionAt;

    @PrePersist
    public void prePersist() {
        if (decisionAt == null) {
            decisionAt = OffsetDateTime.now();
        }
        if (status == null) {
            status = "PENDING_REVIEW";
        }
        if (decision == null) {
            decision = "PENDING_REVIEW";
        }
    }
}
