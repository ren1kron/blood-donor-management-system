package ifmo.se.coursach_back.lab.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface PendingSampleProjection {
    UUID getSampleId();

    String getSampleCode();

    String getStatus();

    OffsetDateTime getCollectedAt();

    UUID getDonationId();

    UUID getDonorId();

    String getDonorFullName();
}
