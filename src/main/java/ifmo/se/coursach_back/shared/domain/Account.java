package ifmo.se.coursach_back.shared.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "account_role",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    public String getFullName() {
        ArrayList<String> parts = new ArrayList<>(3);
        if (lastName != null && !lastName.isBlank()) {
            parts.add(lastName.trim());
        }
        if (firstName != null && !firstName.isBlank()) {
            parts.add(firstName.trim());
        }
        if (middleName != null && !middleName.isBlank()) {
            parts.add(middleName.trim());
        }
        if (parts.isEmpty()) {
            return null;
        }
        return String.join(" ", parts);
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            lastName = null;
            firstName = null;
            middleName = null;
            return;
        }

        String[] parts = fullName.trim().split("\\s+");
        lastName = normalizeNamePart(parts[0]);
        firstName = parts.length > 1 ? normalizeNamePart(parts[1]) : null;

        if (parts.length > 2) {
            StringBuilder middle = new StringBuilder(parts[2]);
            for (int i = 3; i < parts.length; i++) {
                middle.append(' ').append(parts[i]);
            }
            middleName = normalizeNamePart(middle.toString());
        } else {
            middleName = null;
        }
    }

    private String normalizeNamePart(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
