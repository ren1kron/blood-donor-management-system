package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.api.dto.AdminDonorSummaryResponse;
import ifmo.se.coursach_back.admin.application.AdminAccountService;
import ifmo.se.coursach_back.admin.application.result.DonorSummaryResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of ListDonorsUseCase that delegates to AdminAccountService.
 */
@Service
@RequiredArgsConstructor
public class ListDonorsService implements ListDonorsUseCase {
    private final AdminAccountService adminAccountService;

    @Override
    public List<DonorSummaryResult> execute() {
        List<AdminDonorSummaryResponse> responses = adminAccountService.listDonors();
        return responses.stream()
                .map(response -> new DonorSummaryResult(
                        response.donorId(),
                        response.fullName(),
                        response.donorStatus() != null ? response.donorStatus().name() : null,
                        response.email(),
                        response.phone(),
                        response.lastDonationAt(),
                        response.lastAdmissionAt()
                ))
                .toList();
    }
}
