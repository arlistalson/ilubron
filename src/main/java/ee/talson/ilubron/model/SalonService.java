package ee.talson.ilubron.model;

import jakarta.persistence.*;

@Entity
@Table(name = "services")
public class SalonService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    /** Salongi enda kategooria nimi, nt "Juuksur", "Kosmeetik" */
    @Column(nullable = false)
    private String category;

    /** e.g. "35 €" or "70–100 € olenevalt pikkusest" */
    @Column(nullable = false)
    private String priceText;

    @Column(nullable = false)
    private int durationMinutes;

    private boolean active = true;

    public SalonService() {
    }

    public SalonService(String name, String category, String priceText, int durationMinutes) {
        this.name = name;
        this.category = category;
        this.priceText = priceText;
        this.durationMinutes = durationMinutes;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getPriceText() { return priceText; }
    public void setPriceText(String priceText) { this.priceText = priceText; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
