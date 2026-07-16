package ee.talson.ilubron.repository;

import ee.talson.ilubron.model.DayOff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DayOffRepository extends JpaRepository<DayOff, Long> {

    boolean existsByWorkerIdAndDate(Long workerId, LocalDate date);

    Optional<DayOff> findByWorkerIdAndDate(Long workerId, LocalDate date);

    List<DayOff> findByWorkerIdAndDateGreaterThanEqualOrderByDate(Long workerId, LocalDate from);
}
