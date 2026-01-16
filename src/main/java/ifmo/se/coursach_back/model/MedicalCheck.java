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

    @ManyToOne(optional = false)
    @JoinColumn(name = "performed_by_staff_id", nullable = false)
    private StaffProfile performedBy;

    @Column(name = "weight_kg")
    private BigDecimal weightKg;

    @Column(name = "hemoglobin_g_l")
    private BigDecimal hemoglobinGl;

    @Column(name = "systolic_mmhg")
    private Integer systolicMmhg;

    @Column(name = "diastolic_mmhg")
    private Integer diastolicMmhg;

    @Column(nullable = false)
    private String decision;

    @Column(name = "decision_at", nullable = false)
    private OffsetDateTime decisionAt;

    @PrePersist
    public void prePersist() {
        if (decisionAt == null) {
            decisionAt = OffsetDateTime.now();
        }
    }
}
