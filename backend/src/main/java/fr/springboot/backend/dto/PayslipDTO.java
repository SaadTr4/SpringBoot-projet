package fr.springboot.backend.dto;
import java.math.BigDecimal;

public class PayslipDTO {
    private Integer id;
    private Integer month;
    private Integer year;
    private BigDecimal salaireBase;
    private BigDecimal prime;
    private BigDecimal deduction;
    private String employeNom;

    // ======================
    // Constructeurs
    // ======================
    public PayslipDTO() {}

    public PayslipDTO(Integer id, Integer month, Integer year, BigDecimal salaireBase,
                      BigDecimal prime, BigDecimal deduction, String employeNom) {
        this.id = id;
        this.month = month;
        this.year = year;
        this.salaireBase = salaireBase;
        this.prime = prime;
        this.deduction = deduction;
        this.employeNom = employeNom;
    }

    // ======================
    // Getters & Setters
    // ======================
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public BigDecimal getSalaireBase() { return salaireBase; }
    public void setSalaireBase(BigDecimal salaireBase) { this.salaireBase = salaireBase; }

    public BigDecimal getPrime() { return prime; }
    public void setPrime(BigDecimal prime) { this.prime = prime; }

    public BigDecimal getDeduction() { return deduction; }
    public void setDeduction(BigDecimal deduction) { this.deduction = deduction; }

    public String getEmployeNom() { return employeNom; }
    public void setEmployeNom(String employeNom) { this.employeNom = employeNom; }
}
