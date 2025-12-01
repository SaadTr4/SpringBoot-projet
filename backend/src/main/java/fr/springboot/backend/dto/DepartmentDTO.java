package fr.springboot.backend.dto;

public class DepartmentDTO {

    private Integer id;
    private String name;
    private String code;
    private String description;
    private String head;       // nom du chef
    private long nbEmployees;  // nombre de membres

    // --- CONSTRUCTEUR COMPLET ---
    public DepartmentDTO(Integer id, String name, String code, String description,
                         long nbEmployees, String head) {

        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.nbEmployees = nbEmployees;
        this.head = head;
    }

    // --- GETTERS / SETTERS ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getHead() { return head; }
    public void setHead(String head) { this.head = head; }

    public long getNbEmployees() { return nbEmployees; }
    public void setNbEmployees(long nbEmployees) { this.nbEmployees = nbEmployees; }
}
