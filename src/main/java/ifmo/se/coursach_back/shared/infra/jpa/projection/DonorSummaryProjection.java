package ifmo.se.coursach_back.shared.infra.jpa.projection;

import ifmo.se.coursach_back.donor.application.ports.DonorSummary;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Projection interface for donor summary data.
 * Used for efficient queries without loading full entity graphs.
 * Extends DonorSummary port interface for compatibility with application layer.
 */
public interface DonorSummaryProjection extends DonorSummary {

    UUID getDonorId();

    String getFullName();

    String getDonorStatus();

    String getEmail();

    String getPhone();

    Instant getLastDonationAtRaw();

    Instant getLastAdmittedAtRaw();

    @Override
    default OffsetDateTime getLastDonationAt() {
        return toOffsetDateTime(getLastDonationAtRaw());
    }

    @Override
    default OffsetDateTime getLastAdmittedAt() {
        return toOffsetDateTime(getLastAdmittedAtRaw());
    }

    private static OffsetDateTime toOffsetDateTime(Instant value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }
}
