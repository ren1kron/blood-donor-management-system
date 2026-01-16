package ifmo.se.coursach_back.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lab_test_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabTestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sample_id", nullable = false)
    private Sample sample;

    @ManyToOne(optional = false)
    @JoinColumn(name = "test_type_id", nullable = false)
    private LabTestType testType;

    @ManyToOne
    @JoinColumn(name = "labtech_staff_id")
    private StaffProfile labTech;

    @Column(name = "result_value")
    private String resultValue;

    @Column(name = "result_flag", nullable = false)
    private String resultFlag;

    @Column(name = "tested_at", nullable = false)
    private OffsetDateTime testedAt;

    @Column(name = "is_published", nullable = false)
    private boolean published = false;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    @PrePersist
    public void prePersist() {
        if (testedAt == null) {
            testedAt = OffsetDateTime.now();
        }
    }
}
