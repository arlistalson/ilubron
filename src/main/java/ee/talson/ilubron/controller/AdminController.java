package ee.talson.ilubron.controller;

import ee.talson.ilubron.dto.Dtos.BookingDTO;
import ee.talson.ilubron.exception.NotFoundException;
import ee.talson.ilubron.model.Booking;
import ee.talson.ilubron.repository.BookingRepository;
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
    private final String adminToken;

    public AdminController(BookingRepository bookingRepository,
                           @Value("${ilubron.admin-token}") String adminToken) {
        this.bookingRepository = bookingRepository;
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
