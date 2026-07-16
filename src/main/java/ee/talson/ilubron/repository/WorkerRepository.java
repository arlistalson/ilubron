package ee.talson.ilubron.repository;

import ee.talson.ilubron.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

    List<Worker> findByActiveTrueAndCategoriesContaining(String category);
}
