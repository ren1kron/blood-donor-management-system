package ifmo.se.coursach_back.donor.domain;
import ifmo.se.coursach_back.shared.domain.Account;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import jakarta.persistence.Transient;
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

    @Transient
    private String pendingFullName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "blood_group")
    @Convert(converter = BloodGroupConverter.class)
    private BloodGroup bloodGroup;

    @Column(name = "rh_factor")
    @Convert(converter = RhFactorConverter.class)
    private RhFactor rhFactor;

    @Enumerated(EnumType.STRING)
    @Column(name = "donor_status", nullable = false)
    private DonorStatus donorStatus;

    @PrePersist
    public void prePersist() {
        if (donorStatus == null) {
            donorStatus = DonorStatus.POTENTIAL;
        }
    }

    @Transient
    public String getFullName() {
        if (account != null && account.getFullName() != null) {
            return account.getFullName();
        }
        return pendingFullName;
    }

    public void setFullName(String fullName) {
        if (account == null) {
            pendingFullName = fullName;
            return;
        }
        account.setFullName(fullName);
        pendingFullName = account.getFullName();
    }

    public void setAccount(Account account) {
        this.account = account;
        if (this.account != null && this.account.getFullName() == null && pendingFullName != null) {
            this.account.setFullName(pendingFullName);
        }
    }
}
