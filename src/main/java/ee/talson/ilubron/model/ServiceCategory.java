package ee.talson.ilubron.model;

public enum ServiceCategory {
    JUUKSUR("Juuksur"),
    MASSAAZ("Massaaž"),
    RIPSMED("Ripsmed"),
    PEDIKYYR("Pediküür");

    private final String displayName;

    ServiceCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
