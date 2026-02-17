package ifmo.se.coursach_back.lab.application.usecase;

import ifmo.se.coursach_back.lab.application.result.BloodUnitResult;
import java.util.List;

/**
 * Use case for listing blood units pending lab review.
 */
public interface ListPendingBloodUnitsUseCase {
    List<BloodUnitResult> execute();
}
