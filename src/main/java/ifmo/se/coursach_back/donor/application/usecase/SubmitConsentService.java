package ifmo.se.coursach_back.donor.application.usecase;

import ifmo.se.coursach_back.donor.api.dto.ConsentRequest;
import ifmo.se.coursach_back.donor.application.DonorService;
import ifmo.se.coursach_back.donor.application.command.SubmitConsentCommand;
import ifmo.se.coursach_back.donor.application.result.ConsentResult;
import ifmo.se.coursach_back.donor.domain.Consent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubmitConsentService implements SubmitConsentUseCase {
    private final DonorService donorService;

    @Override
    public ConsentResult execute(SubmitConsentCommand command) {
        ConsentRequest request = new ConsentRequest(
                command.visitId(),
                command.bookingId(),
                command.consentType()
        );
        Consent consent = donorService.createConsent(command.accountId(), request);
        return new ConsentResult(
                consent.getId(),
                consent.getVisit().getId(),
                consent.getDonor().getId(),
                consent.getConsentType() != null ? consent.getConsentType().getValue() : null,
                consent.getGivenAt()
        );
    }
}
