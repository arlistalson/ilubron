package ee.talson.ilubron.service;

import ee.talson.ilubron.model.Booking;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    private final JavaMailSender mailSender;
    private final boolean enabled;
    private final String fromAddress;
    private final String fromName;
    private final String salonEmail;

    public NotificationService(JavaMailSender mailSender,
                               @Value("${ilubron.mail.enabled}") boolean enabled,
                               @Value("${ilubron.mail.from}") String fromAddress,
                               @Value("${ilubron.mail.from-name}") String fromName,
                               @Value("${ilubron.mail.salon-email}") String salonEmail) {
        this.mailSender = mailSender;
        this.enabled = enabled;
        this.fromAddress = fromAddress;
        this.fromName = fromName;
        this.salonEmail = salonEmail;
    }

    /** Sends the new-booking notification to the salon and the assigned worker. Failures are logged, never thrown. */
    @Async
    public void notifyNewBooking(Booking b) {
        if (!enabled) {
            log.info("Mail disabled; skipping notification for booking {}", b.getId());
            return;
        }
        List<String> to = new ArrayList<>();
        if (salonEmail != null && !salonEmail.isBlank()) to.add(salonEmail);
        String workerEmail = b.getWorker().getEmail();
        if (workerEmail != null && !workerEmail.isBlank() && !to.contains(workerEmail)) to.add(workerEmail);
        if (to.isEmpty()) return;

        String body = """
                Uus broneering Ilutegu salongis

                Teenus:      %s (%s)
                Teenindaja:  %s
                Kuupäev:     %s
                Kellaaeg:    %s–%s

                Klient:      %s
                Telefon:     %s
                Lisainfo:    %s
                """.formatted(
                b.getService().getName(), b.getService().getPriceText(),
                b.getWorker().getName(),
                b.getDate().format(DATE),
                b.getStartTime().format(TIME), b.getEndTime().format(TIME),
                b.getCustomerName(), b.getCustomerPhone(),
                b.getNotes() == null || b.getNotes().isBlank() ? "-" : b.getNotes());

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(new InternetAddress(fromAddress, fromName));
            helper.setTo(to.toArray(String[]::new));
            helper.setSubject("Uus broneering – " + b.getService().getName() + " (" + b.getWorker().getName() + ")");
            helper.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send booking notification for booking {}", b.getId(), e);
        }
    }
}
