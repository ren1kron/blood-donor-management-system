package ifmo.se.coursach_back.medical.application.ports;

import ifmo.se.coursach_back.medical.domain.AdverseReaction;

/**
 * Port interface for AdverseReaction repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface AdverseReactionRepositoryPort {
    AdverseReaction save(AdverseReaction adverseReaction);
}
