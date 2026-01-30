package ifmo.se.coursach_back.shared.infra.jpa.projection;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Projection interface for donor summary data.
 * Used for efficient queries without loading full entity graphs.
 */
public interface DonorSummaryProjection {

    UUID getDonorId();

    String getFullName();

    String getDonorStatus();

    String getEmail();

    String getPhone();

    OffsetDateTime getLastDonationAt();

    OffsetDateTime getLastAdmittedAt();
}
