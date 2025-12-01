package fr.springboot.backend.enums;

/**
 * Enumeration representing different professional grades/levels.
 * Each grade has a display name for user interface representation.
 */
public enum Grade {
    JUNIOR("Junior"),
    SENIOR("Senior"),
    EXPERT("Expert");

    private final String displayName;

    Grade(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name of the grade for user interface.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
}