package ifmo.se.coursach_back.admin.domain;
import ifmo.se.coursach_back.shared.domain.Account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "staff_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private Account account;

    @Transient
    private String pendingFullName;

    @Column(name = "staff_kind", nullable = false)
    private String staffKind;

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
