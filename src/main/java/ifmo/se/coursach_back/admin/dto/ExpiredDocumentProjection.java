package ifmo.se.coursach_back.admin.dto;

import java.time.LocalDate;
import java.util.UUID;

public interface ExpiredDocumentProjection {
    UUID getDocumentId();

    UUID getDonorId();

    String getFullName();

    String getPhone();

    String getEmail();

    String getDocType();

    LocalDate getExpiresAt();
}
