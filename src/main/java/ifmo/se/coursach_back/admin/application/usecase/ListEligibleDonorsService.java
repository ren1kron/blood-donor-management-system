package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.api.dto.EligibleDonorResponse;
import ifmo.se.coursach_back.admin.application.AdminService;
import ifmo.se.coursach_back.admin.application.result.EligibleDonorResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of ListEligibleDonorsUseCase that delegates to AdminService.
 */
@Service
@RequiredArgsConstructor
public class ListEligibleDonorsService implements ListEligibleDonorsUseCase {
    private final AdminService adminService;

    @Override
    public List<EligibleDonorResult> execute(int minDaysSinceDonation) {
        List<EligibleDonorResponse> responses = adminService.listEligibleDonors(minDaysSinceDonation);
        return responses.stream()
                .map(response -> new EligibleDonorResult(
                        response.donorId(),
                        response.fullName(),
                        response.phone(),
                        response.email(),
                        response.lastDonationAt(),
                        response.daysSinceDonation()
                ))
                .toList();
    }
}
