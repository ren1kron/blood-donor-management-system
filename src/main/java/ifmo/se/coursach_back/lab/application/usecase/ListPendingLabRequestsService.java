package ifmo.se.coursach_back.lab.application.usecase;

import ifmo.se.coursach_back.lab.application.LabWorkflowService;
import ifmo.se.coursach_back.lab.application.result.LabExaminationRequestResult;
import ifmo.se.coursach_back.lab.domain.LabExaminationRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of ListPendingLabRequestsUseCase that delegates to LabWorkflowService.
 */
@Service
@RequiredArgsConstructor
public class ListPendingLabRequestsService implements ListPendingLabRequestsUseCase {
    private final LabWorkflowService labWorkflowService;

    @Override
    public LabExaminationRequestResult execute() {
        List<LabExaminationRequest> requests = labWorkflowService.listPendingRequests();
        List<LabExaminationRequestResult.LabExaminationRequestItem> items = requests.stream()
                .map(r -> {
                    var booking = r.getVisit().getBooking();
                    var slot = booking.getSlot();
                    return new LabExaminationRequestResult.LabExaminationRequestItem(
                            r.getId(),
                            r.getStatus(),
                            r.getVisit().getId(),
                            booking.getId(),
                            booking.getDonor().getId(),
                            booking.getDonor().getFullName(),
                            slot != null ? slot.getStartAt() : null,
                            slot != null ? slot.getEndAt() : null,
                            slot != null ? slot.getLocation() : null,
                            r.getRequestedAt(),
                            r.getCompletedAt(),
                            r.getHemoglobinGl(),
                            r.getHematocritPct(),
                            r.getRbc10e12L()
                    );
                })
                .toList();
        return new LabExaminationRequestResult(items);
    }
}
