package ifmo.se.coursach_back.lab.application.usecase;

import ifmo.se.coursach_back.lab.application.LabWorkflowService;
import ifmo.se.coursach_back.lab.application.result.PendingSampleResult;
import ifmo.se.coursach_back.medical.domain.Sample;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of ListPendingSamplesUseCase that delegates to LabWorkflowService.
 */
@Service
@RequiredArgsConstructor
public class ListPendingSamplesService implements ListPendingSamplesUseCase {
    private final LabWorkflowService labWorkflowService;

    @Override
    public PendingSampleResult execute(String status) {
        List<Sample> samples = labWorkflowService.listPendingSamples(status);
        List<PendingSampleResult.PendingSampleItem> items = samples.stream()
                .map(s -> {
                    UUID donationId = s.getDonation() != null ? s.getDonation().getId() : null;
                    UUID donorId = null;
                    String donorFullName = null;
                    if (s.getDonation() != null && s.getDonation().getVisit() != null
                            && s.getDonation().getVisit().getBooking() != null
                            && s.getDonation().getVisit().getBooking().getDonor() != null) {
                        donorId = s.getDonation().getVisit().getBooking().getDonor().getId();
                        donorFullName = s.getDonation().getVisit().getBooking().getDonor().getFullName();
                    }
                    return new PendingSampleResult.PendingSampleItem(
                            s.getId(),
                            s.getSampleCode(),
                            s.getStatus(),
                            s.getCollectedAt(),
                            donationId,
                            donorId,
                            donorFullName,
                            s.getQuarantineReason(),
                            s.getRejectionReason()
                    );
                })
                .toList();
        return new PendingSampleResult(items);
    }
}
