package ifmo.se.coursach_back.donor.application.usecase;

import ifmo.se.coursach_back.donor.api.dto.DonorProfileResponse;
import ifmo.se.coursach_back.donor.application.DonorService;
import ifmo.se.coursach_back.donor.application.result.DonorProfileResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetDonorProfileService implements GetDonorProfileUseCase {
    private final DonorService donorService;

    @Override
    public DonorProfileResult execute(UUID accountId) {
        DonorProfileResponse response = donorService.getProfile(accountId);
        return new DonorProfileResult(
                response.accountId(),
                response.donorId(),
                response.fullName(),
                response.birthDate(),
                response.bloodGroup(),
                response.rhFactor(),
                response.donorStatus(),
                response.email(),
                response.phone()
        );
    }
}
