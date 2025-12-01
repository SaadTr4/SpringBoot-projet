package fr.springboot.backend;

import fr.springboot.backend.enums.*;
import fr.springboot.backend.model.*;
import fr.springboot.backend.repository.*;
import fr.springboot.backend.service.ProjectService;
import fr.springboot.backend.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Component for initializing sample data in the database on application startup.
 * Creates departments, positions, users, projects, and payslips for testing.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final ProjectRepository projectRepository;
    private final PayslipRepository payslipRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor with dependency injection for all required repositories and services.
     *
     * @param userRepository the user repository
     * @param departmentRepository the department repository
     * @param positionRepository the position repository
     * @param projectRepository the project repository
     * @param payslipRepository the payslip repository
     * @param passwordEncoder the password encoder
     */
    public DataInitializer(
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            PositionRepository positionRepository,
            ProjectRepository projectRepository,
            PayslipRepository payslipRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.projectRepository = projectRepository;
        this.payslipRepository = payslipRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Executes on application startup to initialize sample data.
     *
     * @param args command line arguments
     */
    @Override
    public void run(String... args) {

        // ⚠️ STOP IF DATABASE ALREADY CONTAINS USERS!
        if (userRepository.count() > 0) {
            System.out.println("➡️ Base de données déjà initialisée, aucun chargement effectué.");
            return;
        }

        System.out.println("🚀 Initialisation de la base de données Spring Boot...");

        // ==========================
        // DEPARTMENTS
        // ==========================
        Department informatique = departmentRepository.save(new Department("Département Informatique", "IT", "Gestion des systèmes informatiques et développement"));
        Department rh = departmentRepository.save(new Department("Ressources Humaines", "RH", "Gestion des ressources humaines"));
        Department finance = departmentRepository.save(new Department("Finance", "FIN", "Finance et comptabilité"));

        // ==========================
        // POSITIONS
        // ==========================
        Position devBackend = positionRepository.save(new Position("Développeur Backend", "Développement backend"));
        Position devFrontend = positionRepository.save(new Position("Développeur Frontend", "Interfaces utilisateur"));
        Position chefProjet = positionRepository.save(new Position("Chef de Projet", "Gestion de projet"));
        Position chefDepartement = positionRepository.save(new Position("Chef de Département", "Responsabilité départementale"));
        Position adminSystem = positionRepository.save(new Position("Administrateur Système", "Gestion des systèmes"));
        Position responsableRH = positionRepository.save(new Position("Responsable RH", "Gestion des RH"));

        // ==========================
        // USERS
        // ==========================

        User jean_claude = saveUser(
                "EMP001", "Ilboudo", "Jean-Claude", "jean_claude.ilboudo@company.com",
                ContractType.APPRENTICESHIP, Grade.SENIOR, Role.CHEF_PROJET,
                informatique, chefProjet, "4000.00", "0612345678"
        );

        User saad = saveUser(
                "EMP002", "Tarmidi", "Saad", "saad.tarmidi@company.com",
                ContractType.PERMANENT_FULL_TIME, Grade.JUNIOR, Role.EMPLOYE,
                rh, devBackend, "3000.00", "0698765432"
        );

        User adam = saveUser(
                "EMP003", "Swiczka", "Adam", "adam.swiczka@company.com",
                ContractType.FIXED_TERM_FULL_TIME, Grade.EXPERT, Role.CHEF_DEPARTEMENT,
                finance, chefDepartement, "5000.00", "0678901234"
        );

        User haitam = saveUser(
                "EMP004", "Hania", "Haitam", "haitam.hania@company.com",
                ContractType.PERMANENT_FULL_TIME, Grade.SENIOR, Role.ADMINISTRATEUR,
                informatique, adminSystem, "4500.00", "0654321098"
        );

        User medhi = saveUser(
                "EMP005", "Nom", "Medhi", "medhi.nom@company.com",
                ContractType.PERMANENT_PART_TIME, Grade.JUNIOR, Role.EMPLOYE,
                finance, devFrontend, "2800.00", "0643210987"
        );

        User chefSup = saveUser(
                "EMP006", "Anonyme1", "PM", "pm@company.com",
                ContractType.TEMPORARY_AGENCY, Grade.EXPERT, Role.CHEF_PROJET,
                finance, chefProjet, "6000.00", "0600000000"
        );

        User chefRH = saveUser(
                "EMP007", "Anonyme2", "ResponsableRH", "responsablerh@company.com",
                ContractType.TEMPORARY_AGENCY, Grade.SENIOR, Role.CHEF_DEPARTEMENT,
                rh, responsableRH, "5000.00", "0624194672"
        );

        // ==========================
        // PROJECTS
        // ==========================
        Project siteWeb = projectRepository.save(
                new Project("Refonte du Site Web", jean_claude, "Refonte complète du site web", Status.IN_PROGRESS)
        );
        Project mobileApp = projectRepository.save(
                new Project("Application Mobile", haitam, "Application mobile interne", Status.PLANNED)
        );
        Project cloudMigration = projectRepository.save(
                new Project("Migration Cloud", jean_claude, "Migration vers AWS", Status.IN_PROGRESS)
        );

        // Assignments
        siteWeb.getUsers().add(saad);
        siteWeb.getUsers().add(jean_claude);

        mobileApp.getUsers().add(haitam);
        mobileApp.getUsers().add(medhi);

        cloudMigration.getUsers().add(jean_claude);
        cloudMigration.getUsers().add(medhi);

        projectRepository.save(siteWeb);
        projectRepository.save(mobileApp);
        projectRepository.save(cloudMigration);

        // ==========================
        // PAYSLIPS
        // ==========================
        payslipRepository.save(new Payslip(2023, 11, new BigDecimal("500.00"), new BigDecimal("1000.00"), jean_claude));
        payslipRepository.save(new Payslip(2024, 10, new BigDecimal("200.00"), new BigDecimal("600.00"), saad));
        payslipRepository.save(new Payslip(2024, 10, new BigDecimal("1000.00"), new BigDecimal("1400.00"), adam));
        payslipRepository.save(new Payslip(2023, 12, new BigDecimal("300.00"), new BigDecimal("800.00"), haitam));
        payslipRepository.save(new Payslip(2024, 9, new BigDecimal("250.00"), new BigDecimal("650.00"), medhi));
        payslipRepository.save(new Payslip(2024, 8, new BigDecimal("150.00"), new BigDecimal("350.00"), medhi));

        System.out.println("🎉 Base de données initialisée avec succès !");
    }

    /**
     * Helper method to create and save a user with the specified attributes.
     *
     * @param matricule the registration number
     * @param lastName the last name
     * @param firstName the first name
     * @param email the email address
     * @param contractType the contract type
     * @param grade the grade
     * @param role the role
     * @param dep the department
     * @param pos the position
     * @param salary the base salary
     * @param phone the phone number
     * @return the saved user
     */
    private User saveUser(
            String matricule,
            String lastName,
            String firstName,
            String email,
            ContractType contractType,
            Grade grade,
            Role role,
            Department dep,
            Position pos,
            String salary,
            String phone
    ) {
        User u = new User();
        u.setMatricule(matricule);
        u.setLastName(lastName);
        u.setFirstName(firstName);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode("password123"));
        u.setContractType(contractType);
        u.setGrade(grade);
        u.setRole(role);
        u.setDepartment(dep);
        u.setPosition(pos);
        u.setBaseSalary(new BigDecimal(salary));
        u.setPhone(phone);
        return userRepository.save(u);
    }
}
