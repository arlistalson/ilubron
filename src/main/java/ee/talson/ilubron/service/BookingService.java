package ee.talson.ilubron.service;

import ee.talson.ilubron.dto.Dtos.BookingRequest;
import ee.talson.ilubron.exception.ConflictException;
import ee.talson.ilubron.exception.NotFoundException;
import ee.talson.ilubron.model.Booking;
import ee.talson.ilubron.model.SalonService;
import ee.talson.ilubron.model.Worker;
import ee.talson.ilubron.repository.BookingRepository;
import ee.talson.ilubron.repository.SalonServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SalonServiceRepository serviceRepository;
    private final AvailabilityService availabilityService;

    public BookingService(BookingRepository bookingRepository,
                          SalonServiceRepository serviceRepository,
                          AvailabilityService availabilityService) {
        this.bookingRepository = bookingRepository;
        this.serviceRepository = serviceRepository;
        this.availabilityService = availabilityService;
    }

    @Transactional
    public Booking create(BookingRequest request) {
        SalonService service = serviceRepository.findById(request.serviceId())
                .orElseThrow(() -> new NotFoundException("Teenust ei leitud: " + request.serviceId()));

        LocalTime end = request.startTime().plusMinutes(service.getDurationMinutes());

        for (Worker worker : availabilityService.resolveWorkers(service, request.workerId())) {
            if (!worker.getCategories().contains(service.getCategory())
                    || !worker.getWorkDays().contains(request.date().getDayOfWeek())
                    || worker.getWorkStart().isAfter(request.startTime())
                    || end.isAfter(worker.getWorkEnd())) {
                continue;
            }
            if (availabilityService.isDayOff(worker.getId(), request.date())) {
                if (request.workerId() != null) {
                    throw new ConflictException("Teenindaja ei võta sel päeval broneeringuid.");
                }
                continue;
            }
            // Locked read: concurrent requests for the same worker+date serialize here,
            // so two overlapping bookings cannot both pass the isFree check.
            List<Booking> existing = bookingRepository
                    .findActiveByWorkerAndDateLocked(worker.getId(), request.date());
            if (AvailabilityService.isFree(request.startTime(), end, existing)) {
                Booking booking = new Booking();
                booking.setWorker(worker);
                booking.setService(service);
                booking.setDate(request.date());
                booking.setStartTime(request.startTime());
                booking.setEndTime(end);
                booking.setCustomerName(request.customerName());
                booking.setCustomerPhone(request.customerPhone());
                booking.setCustomerEmail(request.customerEmail());
                booking.setNotes(request.notes());
                return bookingRepository.save(booking);
            }
            if (request.workerId() != null) {
                throw new ConflictException("Valitud aeg on kahjuks juba broneeritud. Palun vali uus aeg.");
            }
        }
        throw new ConflictException("Sobivat vaba aega ei leitud. Palun vali uus aeg.");
    }
}
