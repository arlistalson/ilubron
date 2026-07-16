package ee.talson.ilubron.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "day_offs", uniqueConstraints = @UniqueConstraint(columnNames = {"worker_id", "date"}))
public class DayOff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Worker worker;

    @Column(nullable = false)
    private LocalDate date;

    private String reason;

    public DayOff() {
    }

    public DayOff(Worker worker, LocalDate date, String reason) {
        this.worker = worker;
        this.date = date;
        this.reason = reason;
    }

    public Long getId() { return id; }
    public Worker getWorker() { return worker; }
    public LocalDate getDate() { return date; }
    public String getReason() { return reason; }
}
