package ee.talson.ilubron.controller;

import ee.talson.ilubron.dto.Dtos.AvailabilityDTO;
import ee.talson.ilubron.dto.Dtos.BookingDTO;
import ee.talson.ilubron.dto.Dtos.BookingRequest;
import ee.talson.ilubron.dto.Dtos.ServiceDTO;
import ee.talson.ilubron.dto.Dtos.WorkerDTO;
import ee.talson.ilubron.repository.SalonServiceRepository;
import ee.talson.ilubron.repository.WorkerRepository;
import ee.talson.ilubron.model.Booking;
import ee.talson.ilubron.service.AvailabilityService;
import ee.talson.ilubron.service.BookingService;
import ee.talson.ilubron.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final SalonServiceRepository serviceRepository;
    private final WorkerRepository workerRepository;
    private final AvailabilityService availabilityService;
    private final BookingService bookingService;
    private final NotificationService notificationService;

    public PublicController(SalonServiceRepository serviceRepository,
                            WorkerRepository workerRepository,
                            AvailabilityService availabilityService,
                            BookingService bookingService,
                            NotificationService notificationService) {
        this.serviceRepository = serviceRepository;
        this.workerRepository = workerRepository;
        this.availabilityService = availabilityService;
        this.bookingService = bookingService;
        this.notificationService = notificationService;
    }

    @GetMapping("/services")
    public List<ServiceDTO> services() {
        return serviceRepository.findByActiveTrueOrderByCategoryAscIdAsc()
                .stream().map(ServiceDTO::of).toList();
    }

    @GetMapping("/workers")
    public List<WorkerDTO> workers() {
        return workerRepository.findAll().stream()
                .filter(w -> w.isActive())
                .map(WorkerDTO::of).toList();
    }

    @GetMapping("/availability")
    public AvailabilityDTO availability(@RequestParam Long serviceId,
                                        @RequestParam(required = false) Long workerId,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<LocalTime> slots = availabilityService.freeSlots(serviceId, workerId, date);
        return new AvailabilityDTO(date, slots);
    }

    @PostMapping("/bookings")
    public ResponseEntity<BookingDTO> book(@Valid @RequestBody BookingRequest request) {
        Booking booking = bookingService.create(request);
        notificationService.notifyNewBooking(booking);
        return new ResponseEntity<>(BookingDTO.of(booking), HttpStatus.CREATED);
    }
}
