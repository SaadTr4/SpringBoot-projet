package fr.springboot.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Entity class representing a payslip for an employee.
 * Maps to the "payslip" table in the database.
 */
@Entity
@Table(name = "payslip")
public class Payslip implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "generation_date", nullable = false)
    private LocalDate generationDate;

    @Column(name = "month", nullable = false)
    private Integer month; // 1 = January, 12 = December

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "base_salary", precision = 10, scale = 2)
    private BigDecimal baseSalary;

    @Column(name = "bonuses", precision = 10, scale = 2)
    private BigDecimal bonuses;

    @Column(name = "deductions", precision = 10, scale = 2)
    private BigDecimal deductions;

    @Column(name = "custom_deductions", precision = 10, scale = 2)
    private BigDecimal custom_deductions;

    @Column(name = "net_pay", precision = 10, scale = 2)
    private BigDecimal netPay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_number", referencedColumnName = "registration_number")
    private User user;

    // ===============================
    // Constructors
    // ===============================

    /**
     * Default constructor
     */
    public Payslip() {}

    /**
     * Constructor with parameters
     *
     * @param year The year of the payslip
     * @param month The month of the payslip (1-12)
     * @param bonuses Bonus amount
     * @param custom_deductions Custom deductions amount
     * @param user The user associated with this payslip
     */
    public Payslip(Integer year, Integer month, BigDecimal bonuses, BigDecimal custom_deductions, User user) {
        this.year = year;
        this.month = month;
        this.baseSalary = user.getBaseSalary() != null ? user.getBaseSalary() : BigDecimal.ZERO;
        this.bonuses = bonuses != null ? bonuses : BigDecimal.ZERO;
        this.custom_deductions = custom_deductions != null ? custom_deductions : BigDecimal.ZERO;
        this.user = user;
        this.generationDate = LocalDate.now();
        this.calculateDeductions();
        this.calculateNetPay();
    }

    // ===============================
    // Getters & Setters
    // ===============================

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getGenerationDate() { return generationDate; }
    public void setGenerationDate(LocalDate generationDate) { this.generationDate = generationDate; }
    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public BigDecimal getBaseSalary() { return baseSalary; }
    public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }

    /**
     * Calculates social contributions (13.5% of base salary)
     *
     * @return Social contributions amount
     */
    public BigDecimal getSocialContributions() { return baseSalary.multiply(new BigDecimal("0.135")); }

    /**
     * Calculates CSG/CRDS amount (9.7% of base salary)
     *
     * @return CSG/CRDS amount
     */
    public BigDecimal getCsgCrdsAmount() { return baseSalary.multiply(new BigDecimal("0.097")); }

    /**
     * Calculates gross pay (base salary + bonuses)
     *
     * @return Gross pay amount
     */
    public BigDecimal getGrossPay() { return baseSalary.add(bonuses != null ? bonuses : BigDecimal.ZERO); }

    public BigDecimal getBonuses() { return bonuses; }
    public void setBonuses(BigDecimal bonuses) { this.bonuses = bonuses; }
    public BigDecimal getDeductions() { return deductions; }
    public void setDeductions(BigDecimal deductions) { this.deductions = deductions; }

    public BigDecimal getCustom_deductions() { return custom_deductions; }
    public void setCustom_deductions(BigDecimal custom_deductions) { this.custom_deductions = custom_deductions; }
    public BigDecimal getNetPay() { return netPay; }
    public void setNetPay(BigDecimal netPay) { this.netPay = netPay; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    /**
     * Formats the generation date as string
     *
     * @return Formatted generation date
     */
    public String getFormattedGenerationDate() {
        return formatDate(this.generationDate);
    }

    // ==============================
    // Utility Methods
    // ==============================

    /**
     * Calculates total deductions
     */
    public void calculateDeductions() {
        // social contributions + custom deductions + csg/crds
        this.deductions = getSocialContributions()
                .add(custom_deductions != null ? custom_deductions : BigDecimal.ZERO)
                .add(getCsgCrdsAmount())
        ;
    }

    /**
     * Calculates net pay
     */
    public void calculateNetPay() {
        this.netPay = baseSalary
                .add(bonuses != null ? bonuses : BigDecimal.ZERO)
                .subtract(deductions != null ? deductions : BigDecimal.ZERO);
    }

    /**
     * Formats a date as "dd/MM/yyyy"
     *
     * @param date The date to format
     * @return Formatted date string
     */
    public String formatDate(LocalDate date) {
        if (date == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    /**
     * Formats currency amount in French format
     *
     * @param amount The amount to format
     * @return Formatted currency string
     */
    public String getFormattedCurrency(BigDecimal amount) {
        if (amount == null) return "0,00";
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(java.util.Locale.FRANCE);
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator(' ');
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00", symbols);
        return df.format(amount);
    }

    /**
     * Returns the month name in French
     *
     * @return Month name in French
     */
    public String getMonthName() {
        if (this.month == null || this.month < 1 || this.month > 12) return "";
        String[] months = {"Janvier","Février","Mars","Avril","Mai","Juin",
                "Juillet","Août","Septembre","Octobre","Novembre","Décembre"};
        return months[this.month - 1];
    }

    /**
     * Adds a bonus amount
     *
     * @param amount Bonus amount to add
     */
    public void addBonus(BigDecimal amount) {
        if (amount != null) {
            this.bonuses = this.bonuses.add(amount);
            calculateNetPay();
        }
    }

    /**
     * Resets bonus to zero
     */
    public void clearBonus() {
        this.bonuses = BigDecimal.ZERO;
        calculateNetPay();
    }

    /**
     * Adds a deduction amount
     *
     * @param amount Deduction amount to add
     */
    public void addDeduction(BigDecimal amount) {
        if (amount != null) {
            this.deductions = this.deductions.add(amount);
            calculateNetPay();
        }
    }

    /**
     * Resets deductions to zero
     */
    public void clearDeduction() {
        this.deductions = BigDecimal.ZERO;
        calculateNetPay();
    }

    /**
     * Updates base salary
     *
     * @param amount New base salary amount
     */
    public void updateBaseSalary(BigDecimal amount) {
        if (amount != null) {
            this.baseSalary = amount;
            calculateNetPay();
        }
    }

    @Override
    public String toString() {
        return "Payslip [\n" +
                "                id = " + id + ",\n" +
                "    generationDate = " + generationDate + ",\n" +
                "             month = " + month + ",\n" +
                "              year = " + year + ",\n" +
                "        baseSalary = " + baseSalary + ",\n" +
                "           bonuses = " + bonuses + ",\n" +
                "        deductions = " + deductions + ",\n" +
                "            netPay = " + netPay + ",\n" +
                "              user = " + (user != null ? user.getId() : null) + "\n" +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payslip)) return false;
        Payslip other = (Payslip) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
