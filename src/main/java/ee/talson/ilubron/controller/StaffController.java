package ee.talson.ilubron.controller;

import ee.talson.ilubron.dto.Dtos.BookingDTO;
import ee.talson.ilubron.exception.ConflictException;
import ee.talson.ilubron.exception.NotFoundException;
import ee.talson.ilubron.model.Booking;
import ee.talson.ilubron.model.DayOff;
import ee.talson.ilubron.model.Worker;
import ee.talson.ilubron.repository.BookingRepository;
import ee.talson.ilubron.repository.DayOffRepository;
import ee.talson.ilubron.repository.WorkerRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static ee.talson.ilubron.service.AvailabilityService.SALON_ZONE;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    public record DayOffRequest(@NotNull Long workerId, @NotBlank String pin,
                                @NotNull LocalDate date, String reason) {
    }

    public record DayOffDTO(LocalDate date, String reason, int existingBookings) {
        static DayOffDTO of(DayOff d, int existingBookings) {
            return new DayOffDTO(d.getDate(), d.getReason(), existingBookings);
        }
    }

    private final WorkerRepository workerRepository;
    private final DayOffRepository dayOffRepository;
    private final BookingRepository bookingRepository;

    public StaffController(WorkerRepository workerRepository, DayOffRepository dayOffRepository,
                           BookingRepository bookingRepository) {
        this.workerRepository = workerRepository;
        this.dayOffRepository = dayOffRepository;
        this.bookingRepository = bookingRepository;
    }

    private Worker authorize(Long workerId, String pin) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new NotFoundException("Teenindajat ei leitud: " + workerId));
        if (worker.getPin() == null || worker.getPin().isBlank() || !worker.getPin().equals(pin)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vale PIN-kood");
        }
        return worker;
    }

    @PostMapping("/dayoffs")
    public ResponseEntity<DayOffDTO> closeDay(@Valid @RequestBody DayOffRequest request) {
        Worker worker = authorize(request.workerId(), request.pin());
        if (request.date().isBefore(LocalDate.now(SALON_ZONE))) {
            throw new ConflictException("Möödunud päeva ei saa sulgeda.");
        }
        if (dayOffRepository.existsByWorkerIdAndDate(worker.getId(), request.date())) {
            throw new ConflictException("See päev on juba suletud.");
        }
        DayOff saved = dayOffRepository.save(new DayOff(worker, request.date(), request.reason()));
        // Sulgemine EI tühista juba tehtud broneeringuid – anna töötajale teada, et ta kliente teavitaks
        int existing = bookingRepository.findActiveByWorkerAndDate(worker.getId(), request.date()).size();
        return new ResponseEntity<>(DayOffDTO.of(saved, existing), HttpStatus.CREATED);
    }

    @GetMapping("/dayoffs")
    public List<DayOffDTO> myDayOffs(@RequestParam Long workerId, @RequestParam String pin) {
        Worker worker = authorize(workerId, pin);
        return dayOffRepository
                .findByWorkerIdAndDateGreaterThanEqualOrderByDate(worker.getId(), LocalDate.now(SALON_ZONE))
                .stream()
                .map(d -> DayOffDTO.of(d,
                        bookingRepository.findActiveByWorkerAndDate(worker.getId(), d.getDate()).size()))
                .toList();
    }

    @DeleteMapping("/dayoffs")
    @Transactional
    public ResponseEntity<Void> openDay(@RequestParam Long workerId, @RequestParam String pin,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Worker worker = authorize(workerId, pin);
        DayOff dayOff = dayOffRepository.findByWorkerIdAndDate(worker.getId(), date)
                .orElseThrow(() -> new NotFoundException("Sel kuupäeval pole suletud päeva."));
        dayOffRepository.delete(dayOff);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/bookings")
    public List<BookingDTO> myBookings(@RequestParam Long workerId, @RequestParam String pin) {
        Worker worker = authorize(workerId, pin);
        return bookingRepository.findUpcomingByWorker(worker.getId(), LocalDate.now(SALON_ZONE))
                .stream().map(BookingDTO::of).toList();
    }

    @PatchMapping("/bookings/{id}/cancel")
    public BookingDTO cancelBooking(@PathVariable Long id,
                                    @RequestParam Long workerId, @RequestParam String pin) {
        Worker worker = authorize(workerId, pin);
        Booking booking = bookingRepository.findById(id)
                .filter(b -> b.getWorker().getId().equals(worker.getId()))
                .orElseThrow(() -> new NotFoundException("Broneeringut ei leitud."));
        if (booking.getStatus() == Booking.Status.CANCELLED) {
            throw new ConflictException("See broneering on juba tühistatud.");
        }
        booking.setStatus(Booking.Status.CANCELLED);
        return BookingDTO.of(bookingRepository.save(booking));
    }
}
