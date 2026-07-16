package ee.talson.ilubron.service;

import ee.talson.ilubron.exception.NotFoundException;
import ee.talson.ilubron.model.Booking;
import ee.talson.ilubron.model.SalonService;
import ee.talson.ilubron.model.Worker;
import ee.talson.ilubron.repository.BookingRepository;
import ee.talson.ilubron.repository.DayOffRepository;
import ee.talson.ilubron.repository.SalonServiceRepository;
import ee.talson.ilubron.repository.WorkerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Service
public class AvailabilityService {

    public static final ZoneId SALON_ZONE = ZoneId.of("Europe/Tallinn");
    public static final int SLOT_MINUTES = 30;

    private final WorkerRepository workerRepository;
    private final SalonServiceRepository serviceRepository;
    private final BookingRepository bookingRepository;
    private final DayOffRepository dayOffRepository;

    public AvailabilityService(WorkerRepository workerRepository,
                               SalonServiceRepository serviceRepository,
                               BookingRepository bookingRepository,
                               DayOffRepository dayOffRepository) {
        this.workerRepository = workerRepository;
        this.serviceRepository = serviceRepository;
        this.bookingRepository = bookingRepository;
        this.dayOffRepository = dayOffRepository;
    }

    public boolean isDayOff(Long workerId, LocalDate date) {
        return dayOffRepository.existsByWorkerIdAndDate(workerId, date);
    }

    /**
     * Free start times for a service on a date. With workerId: that worker's free slots.
     * Without: the union of free slots of all workers qualified for the service's category.
     */
    public List<LocalTime> freeSlots(Long serviceId, Long workerId, LocalDate date) {
        SalonService service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Teenust ei leitud: " + serviceId));

        List<Worker> workers = resolveWorkers(service, workerId);
        TreeSet<LocalTime> slots = new TreeSet<>();
        for (Worker worker : workers) {
            List<Booking> existing = bookingRepository.findActiveByWorkerAndDate(worker.getId(), date);
            slots.addAll(freeSlotsForWorker(worker, service, date, existing));
        }
        return new ArrayList<>(slots);
    }

    public List<Worker> resolveWorkers(SalonService service, Long workerId) {
        if (workerId != null) {
            Worker worker = workerRepository.findById(workerId)
                    .orElseThrow(() -> new NotFoundException("Teenindajat ei leitud: " + workerId));
            return List.of(worker);
        }
        return workerRepository.findByActiveTrueAndCategoriesContaining(service.getCategory());
    }

    public List<LocalTime> freeSlotsForWorker(Worker worker, SalonService service,
                                              LocalDate date, List<Booking> existingBookings) {
        List<LocalTime> result = new ArrayList<>();
        if (!worker.isActive()
                || !worker.getCategories().contains(service.getCategory())
                || !worker.getWorkDays().contains(date.getDayOfWeek())
                || isDayOff(worker.getId(), date)) {
            return result;
        }

        ZonedDateTime now = ZonedDateTime.now(SALON_ZONE);
        if (date.isBefore(now.toLocalDate())) {
            return result;
        }

        int duration = service.getDurationMinutes();
        LocalTime slot = worker.getWorkStart();
        while (!slot.plusMinutes(duration).isAfter(worker.getWorkEnd())) {
            boolean inPast = date.equals(now.toLocalDate()) && !slot.isAfter(now.toLocalTime());
            if (!inPast && isFree(slot, slot.plusMinutes(duration), existingBookings)) {
                result.add(slot);
            }
            slot = slot.plusMinutes(SLOT_MINUTES);
        }
        return result;
    }

    public static boolean isFree(LocalTime start, LocalTime end, List<Booking> bookings) {
        return bookings.stream().noneMatch(b ->
                b.getStartTime().isBefore(end) && start.isBefore(b.getEndTime()));
    }
}
