package ifmo.se.coursach_back.donor.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface DonationHistoryProjection {
    UUID getDonationId();

    UUID getVisitId();

    OffsetDateTime getPerformedAt();

    String getDonationType();

    Integer getVolumeMl();
}
