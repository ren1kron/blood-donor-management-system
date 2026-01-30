package ifmo.se.coursach_back.examination.application.usecase;

import ifmo.se.coursach_back.examination.api.dto.ExaminationSlotResponse;
import ifmo.se.coursach_back.examination.application.ExaminationService;
import ifmo.se.coursach_back.examination.application.result.ExaminationSlotResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Implementation of ListExaminationSlotsUseCase that delegates to ExaminationService.
 */
@Service
@RequiredArgsConstructor
public class ListExaminationSlotsService implements ListExaminationSlotsUseCase {
    private final ExaminationService examinationService;

    @Override
    public List<ExaminationSlotResult> execute(OffsetDateTime from, OffsetDateTime to) {
        return examinationService.listAvailableSlots(from, to).stream()
                .map(this::toResult)
                .toList();
    }

    private ExaminationSlotResult toResult(ExaminationSlotResponse response) {
        return new ExaminationSlotResult(
                response.slotId(),
                response.startAt(),
                response.endAt(),
                response.location(),
                response.capacity(),
                response.remainingCapacity()
        );
    }
}
