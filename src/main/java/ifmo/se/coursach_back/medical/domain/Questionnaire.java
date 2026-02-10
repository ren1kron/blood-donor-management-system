package ifmo.se.coursach_back.medical.domain;
import ifmo.se.coursach_back.donor.domain.DonorProfile;
import ifmo.se.coursach_back.appointment.domain.Visit;

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
@Table(name = "questionnaire")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Questionnaire {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    @ManyToOne(optional = false)
    @JoinColumn(name = "donor_id", nullable = false)
    private DonorProfile donor;

    @Column(name = "filled_at", nullable = false)
    private OffsetDateTime filledAt;

    @Column(name = "has_fever", nullable = false)
    private Boolean hasFever;

    @Column(name = "took_antibiotics_last_14d", nullable = false)
    private Boolean tookAntibioticsLast14d;

    @Column(name = "has_chronic_diseases", nullable = false)
    private Boolean hasChronicDiseases;

    @Column(name = "comment")
    private String comment;

    @PrePersist
    public void prePersist() {
        if (filledAt == null) {
            filledAt = OffsetDateTime.now();
        }
    }
}
