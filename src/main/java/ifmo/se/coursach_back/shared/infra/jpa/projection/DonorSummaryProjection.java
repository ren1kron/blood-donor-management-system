package ifmo.se.coursach_back.shared.infra.jpa.projection;

import ifmo.se.coursach_back.donor.application.ports.DonorSummary;
import java.time.OffsetDateTime;
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

    OffsetDateTime getLastDonationAt();

    OffsetDateTime getLastAdmittedAt();
}
