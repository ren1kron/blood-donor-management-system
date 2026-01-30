package ifmo.se.coursach_back.donor.application.usecase;

import ifmo.se.coursach_back.donor.api.dto.DonationHistoryResponse;
import ifmo.se.coursach_back.donor.application.DonorService;
import ifmo.se.coursach_back.donor.application.result.DonationHistoryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListDonationHistoryService implements ListDonationHistoryUseCase {
    private final DonorService donorService;

    @Override
    public List<DonationHistoryResult> execute(UUID accountId) {
        List<DonationHistoryResponse> responses = donorService.listDonationHistory(accountId);
        return responses.stream()
                .map(r -> new DonationHistoryResult(
                        r.donationId(),
                        r.visitId(),
                        r.performedAt(),
                        r.donationType(),
                        r.volumeMl(),
                        r.publishedAt(),
                        r.preVitalsJson(),
                        r.postVitalsJson(),
                        r.hasVitals()
                ))
                .toList();
    }
}
