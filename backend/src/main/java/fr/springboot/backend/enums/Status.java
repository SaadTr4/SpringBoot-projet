package fr.springboot.backend.enums;

/**
 * Enumeration representing different project statuses.
 * Each status has a display name for user interface representation.
 */
public enum Status {
    IN_PROGRESS("En cours"),
    COMPLETED("Terminé"),
    CANCELLED("Annulé"),
    PLANNED("Planifié");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name of the status for user interface.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
}