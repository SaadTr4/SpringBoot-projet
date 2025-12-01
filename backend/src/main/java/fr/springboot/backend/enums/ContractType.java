package fr.springboot.backend.enums;

/**
 * Enumeration representing different types of employment contracts.
 * Each contract type has a display name for user interface representation.
 */
public enum ContractType {
    PERMANENT_FULL_TIME("CDI"),
    PERMANENT_PART_TIME("CDI temps partiel"),
    FIXED_TERM_FULL_TIME("CDD"),
    FIXED_TERM_PART_TIME("CDD temps partiel"),
    TEMPORARY_AGENCY("Intérim"),
    INTERNSHIP("Stage"),
    APPRENTICESHIP("Alternance / Apprentissage"),
    FREELANCE_CONTRACTOR("Indépendant / Freelance"),
    ZERO_HOURS("Contrat zéro heure"),
    SEASONAL("Contrat saisonnier"),
    ON_CALL("Contrat d’astreinte"),
    VOLUNTARY_UNPAID("Bénévolat / Stage non rémunéré");

    private final String displayName;

    ContractType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name of the contract type for user interface.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
}