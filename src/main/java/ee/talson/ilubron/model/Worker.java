package ee.talson.ilubron.model;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "workers")
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<ServiceCategory> categories;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> workDays;

    @Column(nullable = false)
    private LocalTime workStart;

    @Column(nullable = false)
    private LocalTime workEnd;

    private boolean active = true;

    /** Personal code for the staff self-service page (day off management). */
    private String pin;

    public Worker() {
    }

    public Worker(String name, String email, Set<ServiceCategory> categories,
                  Set<DayOfWeek> workDays, LocalTime workStart, LocalTime workEnd) {
        this.name = name;
        this.email = email;
        this.categories = categories;
        this.workDays = workDays;
        this.workStart = workStart;
        this.workEnd = workEnd;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Set<ServiceCategory> getCategories() { return categories; }
    public void setCategories(Set<ServiceCategory> categories) { this.categories = categories; }
    public Set<DayOfWeek> getWorkDays() { return workDays; }
    public void setWorkDays(Set<DayOfWeek> workDays) { this.workDays = workDays; }
    public LocalTime getWorkStart() { return workStart; }
    public void setWorkStart(LocalTime workStart) { this.workStart = workStart; }
    public LocalTime getWorkEnd() { return workEnd; }
    public void setWorkEnd(LocalTime workEnd) { this.workEnd = workEnd; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
}
