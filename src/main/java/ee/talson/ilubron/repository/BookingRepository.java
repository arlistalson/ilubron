package ee.talson.ilubron.repository;

import ee.talson.ilubron.model.Booking;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
            select b from Booking b
            where b.worker.id = :workerId and b.date = :date and b.status <> 'CANCELLED'
            """)
    List<Booking> findActiveByWorkerAndDate(@Param("workerId") Long workerId, @Param("date") LocalDate date);

    /** Same query but with a write lock, used inside the booking transaction to prevent double-booking races. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select b from Booking b
            where b.worker.id = :workerId and b.date = :date and b.status <> 'CANCELLED'
            """)
    List<Booking> findActiveByWorkerAndDateLocked(@Param("workerId") Long workerId, @Param("date") LocalDate date);

    List<Booking> findByDateOrderByStartTime(LocalDate date);

    @Query("""
            select b from Booking b
            where b.worker.id = :workerId and b.date >= :from and b.status <> 'CANCELLED'
            order by b.date, b.startTime
            """)
    List<Booking> findUpcomingByWorker(@Param("workerId") Long workerId, @Param("from") LocalDate from);
}
