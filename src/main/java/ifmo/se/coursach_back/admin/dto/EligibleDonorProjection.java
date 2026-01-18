package ifmo.se.coursach_back.admin.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface EligibleDonorProjection {
    UUID getDonorId();

    String getFullName();

    String getPhone();

    String getEmail();

    OffsetDateTime getLastDonationAt();
}
