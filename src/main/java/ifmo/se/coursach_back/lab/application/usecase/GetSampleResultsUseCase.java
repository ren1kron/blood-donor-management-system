package ifmo.se.coursach_back.lab.application.usecase;

import ifmo.se.coursach_back.lab.application.result.LabTestResultResult;
import java.util.List;
import java.util.UUID;

/**
 * Use case interface for getting lab results by sample.
 */
public interface GetSampleResultsUseCase {
    /**
     * Get all lab test results for a specific sample.
     * @param sampleId the sample ID to get results for
     * @return list of lab test results for the sample
     */
    List<LabTestResultResult> execute(UUID sampleId);
}
