package ifmo.se.coursach_back.donor.domain;
import ifmo.se.coursach_back.shared.domain.Account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "donor_profile")
@Getter
@Setter
@NoArgsConstructor
public class DonorProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private Account account;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "blood_group")
    private String bloodGroup;

    @Column(name = "rh_factor")
    private String rhFactor;

    @Enumerated(EnumType.STRING)
    @Column(name = "donor_status", nullable = false)
    private DonorStatus donorStatus;

    @PrePersist
    public void prePersist() {
        if (donorStatus == null) {
            donorStatus = DonorStatus.POTENTIAL;
        }
    }
}
