package ee.talson.ilubron.controller;

import ee.talson.ilubron.dto.Dtos.BookingDTO;
import ee.talson.ilubron.exception.NotFoundException;
import ee.talson.ilubron.model.Booking;
import ee.talson.ilubron.repository.BookingRepository;
import ee.talson.ilubron.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final BookingRepository bookingRepository;
    private final WorkerRepository workerRepository;
    private final String adminToken;

    public AdminController(BookingRepository bookingRepository,
                           WorkerRepository workerRepository,
                           @Value("${ilubron.admin-token}") String adminToken) {
        this.bookingRepository = bookingRepository;
        this.workerRepository = workerRepository;
        this.adminToken = adminToken;
    }

    private void authorize(String token) {
        if (adminToken.isBlank() || !adminToken.equals(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vale või puuduv admin-token");
        }
    }

    @GetMapping("/bookings")
    public List<BookingDTO> bookings(@RequestHeader(value = "X-Admin-Token", required = false) String token,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        authorize(token);
        return bookingRepository.findByDateOrderByStartTime(date).stream().map(BookingDTO::of).toList();
    }

    @PatchMapping("/workers/{id}/pin")
    public java.util.Map<String, String> setPin(@RequestHeader(value = "X-Admin-Token", required = false) String token,
                                                @PathVariable Long id,
                                                @RequestParam String value) {
        authorize(token);
        var worker = workerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Teenindajat ei leitud: " + id));
        worker.setPin(value);
        workerRepository.save(worker);
        return java.util.Map.of("worker", worker.getName(), "pin", value);
    }

    @PatchMapping("/bookings/{id}/status")
    public BookingDTO setStatus(@RequestHeader(value = "X-Admin-Token", required = false) String token,
                                @PathVariable Long id,
                                @RequestParam Booking.Status value) {
        authorize(token);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Broneeringut ei leitud: " + id));
        booking.setStatus(value);
        return BookingDTO.of(bookingRepository.save(booking));
    }
}
