package ifmo.se.coursach_back.nurse.application.usecase;

import ifmo.se.coursach_back.medical.api.dto.ScheduledDonorResponse;
import ifmo.se.coursach_back.nurse.application.NurseWorkflowService;
import ifmo.se.coursach_back.nurse.application.result.DonationQueueResult;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of ListDonationQueueUseCase that delegates to NurseWorkflowService.
 */
@Service
@RequiredArgsConstructor
public class ListDonationQueueService implements ListDonationQueueUseCase {
    private final NurseWorkflowService nurseWorkflowService;

    @Override
    public DonationQueueResult execute(OffsetDateTime from) {
        List<ScheduledDonorResponse> donors = nurseWorkflowService.listDonationQueue(from);
        List<DonationQueueResult.DonationQueueItem> items = donors.stream()
                .map(d -> new DonationQueueResult.DonationQueueItem(
                        d.bookingId(),
                        d.visitId(),
                        d.donorId(),
                        d.donorFullName(),
                        d.donorStatus(),
                        d.slotId(),
                        d.purpose(),
                        d.startAt(),
                        d.endAt(),
                        d.location(),
                        d.bookingStatus(),
                        d.medicalDecision(),
                        d.hasDonation(),
                        d.canDonate(),
                        d.donationId(),
                        d.donationPublished(),
                        d.collectionSessionId(),
                        d.collectionSessionStatus(),
                        d.collectionSessionStartedAt(),
                        d.collectionSessionEndedAt(),
                        d.collectionSessionNurseName(),
                        d.collectionSessionPreVitalsJson(),
                        d.collectionSessionPostVitalsJson(),
                        d.collectionSessionNotes(),
                        d.collectionSessionComplications(),
                        d.collectionSessionInterruptionReason()
                ))
                .toList();
        return new DonationQueueResult(items);
    }
}
