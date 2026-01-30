package ifmo.se.coursach_back.donor.application.ports;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Domain interface for donor summary data.
 * This is a port interface that can be implemented by infrastructure projections.
 */
public interface DonorSummary {
    UUID getDonorId();
    String getFullName();
    String getDonorStatus();
    String getEmail();
    String getPhone();
    OffsetDateTime getLastDonationAt();
    OffsetDateTime getLastAdmittedAt();
}
