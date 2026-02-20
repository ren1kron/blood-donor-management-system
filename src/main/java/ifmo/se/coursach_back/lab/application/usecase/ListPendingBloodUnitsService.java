package ifmo.se.coursach_back.lab.application.usecase;

import ifmo.se.coursach_back.donor.domain.DonorProfile;
import ifmo.se.coursach_back.lab.application.result.BloodUnitResult;
import ifmo.se.coursach_back.medical.application.ports.BloodUnitRepositoryPort;
import ifmo.se.coursach_back.medical.domain.BloodUnit;
import ifmo.se.coursach_back.medical.domain.Donation;
import ifmo.se.coursach_back.nurse.application.ports.CollectionSessionRepositoryPort;
import ifmo.se.coursach_back.nurse.domain.CollectionSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListPendingBloodUnitsService implements ListPendingBloodUnitsUseCase {
    private final BloodUnitRepositoryPort bloodUnitRepository;
    private final CollectionSessionRepositoryPort collectionSessionRepository;

    @Override
    public List<BloodUnitResult> execute() {
//        List<BloodUnit> units = bloodUnitRepository.findByStatuses(List.of("PENDING_LAB_REVIEW"));
        List<BloodUnit> units = bloodUnitRepository.findAll();
        return units.stream().map(this::toResult).toList();
    }

    private BloodUnitResult toResult(BloodUnit unit) {
        Donation donation = unit.getDonation();
        CollectionSession session = collectionSessionRepository.findByVisitId(donation.getVisit().getId())
                .orElse(null);

        String donorFullName = null;
        DonorProfile donor = null;
        if (donation.getVisit().getBooking() != null && donation.getVisit().getBooking().getDonor() != null) {
            donor = donation.getVisit().getBooking().getDonor();
            donorFullName = donor.getFullName();
        }

        // Fall back to donor profile blood group / rh factor when the blood unit has none
        String bloodGroup = unit.getBloodGroup();
        String rhFactor = unit.getRhFactor();
        if (bloodGroup == null && donor != null && donor.getBloodGroup() != null) {
            bloodGroup = donor.getBloodGroup().getDisplayValue();
        }
        if (rhFactor == null && donor != null && donor.getRhFactor() != null) {
            rhFactor = donor.getRhFactor().getDisplayValue();
        }

        return new BloodUnitResult(
                unit.getId(),
                donation.getId(),
                donation.getVisit().getId(),
                donorFullName,
                donation.getDonationType() != null ? donation.getDonationType().getValue() : null,
                unit.getVolumeMl(),
                bloodGroup,
                rhFactor,
                unit.getComponentType() != null ? unit.getComponentType().getId() : null,
                unit.getComponentType() != null ? unit.getComponentType().getCode() : null,
                unit.getCollectedAt(),
                unit.getExpiresAt(),
                unit.getStatus(),
                unit.getStorageLocation(),
                unit.getQuarantineReason(),
                session != null ? session.getNotes() : null,
                session != null ? session.getComplications() : null,
                session != null ? session.getDonorState() : null
        );
    }
}
