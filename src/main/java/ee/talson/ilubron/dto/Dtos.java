package ee.talson.ilubron.dto;

import ee.talson.ilubron.model.Booking;
import ee.talson.ilubron.model.SalonService;
import ee.talson.ilubron.model.Worker;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public final class Dtos {

    private Dtos() {
    }

    public record ServiceDTO(Long id, String name, String category, String priceText, int durationMinutes) {
        public static ServiceDTO of(SalonService s) {
            return new ServiceDTO(s.getId(), s.getName(), s.getCategory(),
                    s.getPriceText(), s.getDurationMinutes());
        }
    }

    public record WorkerDTO(Long id, String name, List<String> categories) {
        public static WorkerDTO of(Worker w) {
            return new WorkerDTO(w.getId(), w.getName(), List.copyOf(w.getCategories()));
        }
    }

    public record AvailabilityDTO(LocalDate date, List<LocalTime> freeSlots) {
    }

    public record BookingRequest(
            @NotNull Long serviceId,
            Long workerId,          // null = "Esimene vaba"
            @NotNull LocalDate date,
            @NotNull LocalTime startTime,
            @NotBlank String customerName,
            @NotBlank String customerPhone,
            String customerEmail,
            String notes) {
    }

    public record BookingDTO(Long id, String workerName, String serviceName, String priceText,
                             LocalDate date, LocalTime startTime, LocalTime endTime,
                             String customerName, String customerPhone, String customerEmail,
                             String notes, String status) {
        public static BookingDTO of(Booking b) {
            return new BookingDTO(b.getId(), b.getWorker().getName(), b.getService().getName(),
                    b.getService().getPriceText(), b.getDate(), b.getStartTime(), b.getEndTime(),
                    b.getCustomerName(), b.getCustomerPhone(), b.getCustomerEmail(),
                    b.getNotes(), b.getStatus().name());
        }
    }
}
