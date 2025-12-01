package fr.springboot.backend.dto;

import java.math.BigDecimal;

public class PayslipDTO {
    private Integer id;
    private Integer month;
    private Integer year;
    private BigDecimal baseSalary;
    private BigDecimal bonuses;
    private BigDecimal deductions;
    private BigDecimal netPay;
    private String employeNom;
    private BigDecimal customDeductions;

    private String monthName;


    // Constructeur complet
    public PayslipDTO(Integer id, Integer month, Integer year, BigDecimal baseSalary,
                      BigDecimal bonuses, BigDecimal deductions, BigDecimal netPay,
                      BigDecimal customDeductions, String employeNom) {
        this.id = id;
        this.month = month;
        this.year = year;
        this.baseSalary = baseSalary;
        this.bonuses = bonuses;
        this.deductions = deductions;
        this.netPay = netPay;
        this.customDeductions = customDeductions;
        this.employeNom = employeNom;
        this.monthName = null; // sera rempli après
    }
    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public BigDecimal getCustomDeductions() { return customDeductions; }
    public void setCustomDeductions(BigDecimal customDeductions) { this.customDeductions = customDeductions; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public BigDecimal getBaseSalary() { return baseSalary; }
    public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }

    public BigDecimal getBonuses() { return bonuses; }
    public void setBonuses(BigDecimal bonuses) { this.bonuses = bonuses; }

    public BigDecimal getDeductions() { return deductions; }
    public void setDeductions(BigDecimal deductions) { this.deductions = deductions; }

    public BigDecimal getNetPay() { return netPay; }
    public void setNetPay(BigDecimal netPay) { this.netPay = netPay; }

    public String getEmployeNom() { return employeNom; }
    public void setEmployeNom(String employeNom) { this.employeNom = employeNom; }

    private static final String[] MONTHS_FR = {
            "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
    };

    public String getMonthName() {
        return MONTHS_FR[this.month - 1];
    }
}