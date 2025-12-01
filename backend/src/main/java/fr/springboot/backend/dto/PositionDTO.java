package fr.springboot.backend.dto;

import fr.springboot.backend.model.Position;

public class PositionDTO {

    private Integer id;
    private String name;
    private String description;

    public PositionDTO() {}

    public PositionDTO(Position p) {
        this.id = p.getId();
        this.name = p.getName();
        this.description = p.getDescription();
    }

    public PositionDTO(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // GETTERS & SETTERS

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
