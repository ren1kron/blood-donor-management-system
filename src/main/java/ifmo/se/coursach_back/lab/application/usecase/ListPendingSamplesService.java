package ifmo.se.coursach_back.lab.application.usecase;

import ifmo.se.coursach_back.lab.application.LabWorkflowService;
import ifmo.se.coursach_back.lab.application.result.PendingSampleResult;
import ifmo.se.coursach_back.medical.domain.Sample;
import java.util.List;
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
                .map(s -> new PendingSampleResult.PendingSampleItem(
                        s.getId(),
                        s.getSampleCode(),
                        s.getStatus(),
                        s.getCollectedAt()
                ))
                .toList();
        return new PendingSampleResult(items);
    }
}
