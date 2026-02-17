package ifmo.se.coursach_back.lab.application.usecase;

import ifmo.se.coursach_back.audit.application.AuditService;
import ifmo.se.coursach_back.exception.NotFoundException;
import ifmo.se.coursach_back.lab.application.command.ReviewBloodUnitCommand;
import ifmo.se.coursach_back.lab.application.result.BloodUnitResult;
import ifmo.se.coursach_back.medical.application.ports.BloodUnitRepositoryPort;
import ifmo.se.coursach_back.medical.domain.BloodComponentType;
import ifmo.se.coursach_back.medical.domain.BloodUnit;
import ifmo.se.coursach_back.medical.domain.Donation;
import ifmo.se.coursach_back.medical.infra.jpa.BloodComponentTypeRepository;
import ifmo.se.coursach_back.nurse.application.ports.CollectionSessionRepositoryPort;
import ifmo.se.coursach_back.nurse.domain.CollectionSession;
import ifmo.se.coursach_back.admin.application.ports.StaffProfileRepositoryPort;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ReviewBloodUnitService implements ReviewBloodUnitUseCase {
    private static final Set<String> ALLOWED_STATUSES = Set.of(
            "PENDING_LAB_REVIEW", "IN_STOCK", "QUARANTINED", "DISCARDED", "EXPIRED", "ISSUED"
    );

    private final BloodUnitRepositoryPort bloodUnitRepository;
    private final BloodComponentTypeRepository bloodComponentTypeRepository;
    private final CollectionSessionRepositoryPort collectionSessionRepository;
    private final StaffProfileRepositoryPort staffProfileRepository;
    private final AuditService auditService;

    @Override
    @Transactional
    public BloodUnitResult execute(ReviewBloodUnitCommand command) {
        staffProfileRepository.findByAccountId(command.accountId())
                .orElseThrow(() -> new NotFoundException("Staff profile not found"));
        
        BloodUnit unit = bloodUnitRepository.findById(command.bloodUnitId())
                .orElseThrow(() -> new NotFoundException("Blood unit not found"));

        if (command.bloodGroup() != null) {
            unit.setBloodGroup(command.bloodGroup().trim().toUpperCase());
        }
        if (command.rhFactor() != null) {
            unit.setRhFactor(command.rhFactor().trim().toUpperCase());
        }
        if (command.componentTypeId() != null) {
            BloodComponentType componentType = bloodComponentTypeRepository.findById(command.componentTypeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid component type ID"));
            unit.setComponentType(componentType);
        }
        if (command.expiresAt() != null) {
            unit.setExpiresAt(command.expiresAt());
        }
        if (command.status() != null) {
            String status = command.status().trim().toUpperCase();
            if (!ALLOWED_STATUSES.contains(status)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid status. Allowed: " + ALLOWED_STATUSES);
            }
            unit.setStatus(status);
        }
        if (command.storageLocation() != null) {
            unit.setStorageLocation(command.storageLocation().trim());
        }
        if (command.quarantineReason() != null) {
            unit.setQuarantineReason(command.quarantineReason().trim());
        }

        BloodUnit saved = bloodUnitRepository.save(unit);

        auditService.log(command.accountId(), "BLOOD_UNIT_REVIEWED", "BloodUnit", saved.getId(),
                Map.of("status", saved.getStatus()));

        return toResult(saved);
    }

    private BloodUnitResult toResult(BloodUnit unit) {
        Donation donation = unit.getDonation();
        CollectionSession session = collectionSessionRepository.findByVisitId(donation.getVisit().getId())
                .orElse(null);

        String donorFullName = null;
        if (donation.getVisit().getBooking() != null && donation.getVisit().getBooking().getDonor() != null) {
            donorFullName = donation.getVisit().getBooking().getDonor().getFullName();
        }

        return new BloodUnitResult(
                unit.getId(),
                donation.getId(),
                donation.getVisit().getId(),
                donorFullName,
                donation.getDonationType() != null ? donation.getDonationType().getValue() : null,
                unit.getVolumeMl(),
                unit.getBloodGroup(),
                unit.getRhFactor(),
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
