package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.model.Sample;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleRepository extends JpaRepository<Sample, UUID> {
    boolean existsBySampleCode(String sampleCode);

    List<Sample> findByStatusInOrderByCollectedAtAsc(List<String> statuses);
}
