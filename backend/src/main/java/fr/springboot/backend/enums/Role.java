package fr.springboot.backend.enums;

/**
 * Enumeration representing different user roles in the system.
 * Each role has a display name for user interface representation.
 */
public enum Role {
    ADMINISTRATEUR("Administrateur"),
    CHEF_DEPARTEMENT("Chef de Département"),
    CHEF_PROJET("Chef de Projet"),
    EMPLOYE("Employé");


    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name of the role for user interface.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
}