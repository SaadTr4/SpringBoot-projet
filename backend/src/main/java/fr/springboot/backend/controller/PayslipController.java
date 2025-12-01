package fr.springboot.backend.controller;

import fr.springboot.backend.dto.PayslipDTO;
import fr.springboot.backend.model.Payslip;
import fr.springboot.backend.model.User;
import fr.springboot.backend.repository.PayslipRepository;
import fr.springboot.backend.repository.UserRepository;
import fr.springboot.backend.service.PayslipService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing Payslip operations.
 * Provides endpoints for CRUD operations and filtering of payslips.
 */
@RestController
@RequestMapping("/api/payslips")
public class PayslipController {

    private final PayslipService payslipService;
    private final PayslipRepository payslipRepository ;



    /**
     * Constructor with dependency injection.
     *
     * @param payslipService the payslip service
     */
    public PayslipController(PayslipService payslipService, PayslipRepository payslipRepository) {
        this.payslipService = payslipService;
        this.payslipRepository = payslipRepository;
    }

    /**
     * Retrieves all payslips.
     *
     * @return list of all payslips
     */
    @GetMapping
    public List<PayslipDTO> getAll() {
        return payslipService.findAllDTO();
    }

    /**
     * Retrieves payslips for a specific user.
     *
     * @param id the user ID
     * @return list of payslips for the user
     */
    @GetMapping("/user/{id}")
    public List<PayslipDTO> getByUserId(@PathVariable Integer id) {
        return payslipService.findByUserIdDTO(id);
    }

    /**
     * Filters payslips based on criteria.
     *
     * @param matricule the user registration number (optional)
     * @param year the year (optional)
     * @param month the month (optional)
     * @return list of filtered payslips
     */
    @GetMapping("/filter")
    public List<PayslipDTO> filter(
            @RequestParam(required = false) String matricule,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        if (matricule != null) {
            matricule = matricule.trim();
        }
        return payslipService.findFilteredDTO(matricule, year, month);
    }


    /**
     * Creates a new payslip for a user.
     *
     * @param matricule the user registration number
     * @param year the payslip year
     * @param month the payslip month
     * @param bonuses the bonus amount
     * @param deductions the deduction amount
     * @return the created payslip
     */
    @PostMapping("/create")
    public PayslipDTO create(
            @RequestParam String matricule,
            @RequestParam Integer year,
            @RequestParam Integer month,
            @RequestParam BigDecimal bonuses,
            @RequestParam BigDecimal deductions
    ) {
        // Crée la fiche de paie
        Payslip payslip = payslipService.createPayslip(matricule, year, month, bonuses, deductions);

        // Retourne un DTO (pas l’entité complète) pour éviter les boucles JSON
        return payslipService.toDTO(payslip);
    }


    /**
     * Updates an existing payslip.
     *
     * @param id the payslip ID
     * @return the updated payslip
     */
    @PutMapping("/{id}")
    public PayslipDTO update(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> body
    ) {
        System.out.println("=== BACKEND RECEIVED ===");
        System.out.println("Body: " + body);

        BigDecimal bonuses = body.get("bonuses") != null ?
                new BigDecimal(body.get("bonuses").toString()) : null;
        BigDecimal deductions = body.get("deductions") != null ?
                new BigDecimal(body.get("deductions").toString()) : null;
        Integer month = body.get("month") != null ?
                Integer.parseInt(body.get("month").toString()) : null;
        Integer year = body.get("year") != null ?
                Integer.parseInt(body.get("year").toString()) : null;

        // Mise à jour réelle
        payslipService.updatePayslip(id, bonuses, deductions, month, year);

        // Retour du DTO à jour
        PayslipDTO dto = payslipService.findByIdDTO(id);

        System.out.println("=== BACKEND SENDING ===");
        System.out.println("DTO: bonuses=" + dto.getBonuses() +
                ", customDed=" + dto.getCustomDeductions() +
                ", month=" + dto.getMonth() +
                ", year=" + dto.getYear());

        return dto;
    }



    // Endpoint pour le PDF
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> exportPDF(@PathVariable Integer id) throws Exception {
        Payslip payslip = payslipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fiche introuvable"));

        User user = payslip.getUser();
        String html = loadTemplate();

        // Remplacer TOUS les placeholders
        // Remplacez ces 4 lignes:
        html = html.replace("{{department}}", user.getDepartment() != null ? user.getDepartment().getName() : "N/A");
        html = html.replace("{{position}}", user.getPosition() != null ? user.getPosition().getName() : "N/A");
        html = html.replace("{{grade}}", user.getGrade() != null ? user.getGrade().getDisplayName() : "N/A");
        html = html.replace("{{contractType}}", user.getContractType() != null ? user.getContractType().getDisplayName() : "CDI");
        html = html.replace("{{employeeName}}", user.getFirstName() + " " + user.getLastName());
        html = html.replace("{{registrationNumber}}", user.getMatricule());
        html = html.replace("{{email}}", user.getEmail() != null ? user.getEmail() : "N/A");
        html = html.replace("{{phone}}", user.getPhone() != null ? user.getPhone() : "N/A");
        html = html.replace("{{monthName}}", payslip.getMonthName());
        html = html.replace("{{year}}", String.valueOf(payslip.getYear()));
        html = html.replace("{{generationDate}}", payslip.getFormattedGenerationDate());
        html = html.replace("{{baseSalary}}", payslip.getFormattedCurrency(payslip.getBaseSalary()));
        html = html.replace("{{bonuses}}", payslip.getFormattedCurrency(payslip.getBonuses()));
        html = html.replace("{{grossPay}}", payslip.getFormattedCurrency(payslip.getGrossPay()));
        html = html.replace("{{socialContributions}}", payslip.getFormattedCurrency(payslip.getSocialContributions()));
        html = html.replace("{{getCsgCrdsAmount}}", payslip.getFormattedCurrency(payslip.getCsgCrdsAmount()));
        html = html.replace("{{custom_deductions}}", payslip.getFormattedCurrency(payslip.getCustom_deductions()));
        html = html.replace("{{deductions}}", payslip.getFormattedCurrency(payslip.getDeductions()));
        html = html.replace("{{netPay}}", payslip.getFormattedCurrency(payslip.getNetPay()));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(os);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                "Fiche_" + user.getLastName() + "_" + payslip.getMonthName() + "_" + payslip.getYear() + ".pdf");

        return ResponseEntity.ok().headers(headers).body(os.toByteArray());
    }
    /**
     * Deletes a payslip.
     *
     * @param id the payslip ID to delete
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        payslipService.deletePayslip(id);
    }

    private String loadTemplate() throws Exception {
        InputStream is = getClass().getResourceAsStream("/templates/payslip_template.html");
        return new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
    }
}
