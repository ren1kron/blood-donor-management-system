package ifmo.se.coursach_back.lab.application.result;

import ifmo.se.coursach_back.medical.domain.SampleStatus;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Result object for pending samples query.
 */
public record PendingSampleResult(
        List<PendingSampleItem> items
) {
    /**
     * A single pending sample item.
     */
    public record PendingSampleItem(
            UUID sampleId,
            String barcode,
            SampleStatus status,
            OffsetDateTime collectedAt
    ) {
    }
}
