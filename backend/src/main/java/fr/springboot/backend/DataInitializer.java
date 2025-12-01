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

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final ProjectRepository projectRepository;
    private final PayslipRepository payslipRepository;

    private final PasswordEncoder passwordEncoder;

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

    @Override
    public void run(String... args) {

        // ⚠️ STOP SI LA BASE CONTIENT DÉJÀ DES USERS !
        if (userRepository.count() > 0) {
            System.out.println("➡️ Base déjà initialisée, aucun chargement effectué.");
            return;
        }

        System.out.println("🚀 Initialisation de la base Spring Boot...");

        // ==========================
        // DÉPARTEMENTS
        // ==========================
        Department informatique = departmentRepository.save(new Department("Informatique", "IT", "Gestion du SI et dev"));
        Department rh = departmentRepository.save(new Department("Ressources Humaines", "RH", "Gestion RH"));
        Department finance = departmentRepository.save(new Department("Finance", "FIN", "Finance & compta"));

        // ==========================
        // POSTES
        // ==========================
        Position devBackend = positionRepository.save(new Position("Développeur Backend", "Développement backend"));
        Position devFrontend = positionRepository.save(new Position("Développeur Frontend", "Interfaces UI"));
        Position chefProjet = positionRepository.save(new Position("Chef de Projet", "Pilotage projet"));
        Position chefDepartement = positionRepository.save(new Position("Chef de Département", "Responsable département"));
        Position adminSystem = positionRepository.save(new Position("Administrateur Système", "Gestion systèmes"));
        Position responsableRH = positionRepository.save(new Position("Responsable RH", "Gestion RH"));

        // ==========================
        // UTILISATEURS
        // ==========================

        User jean_claude = saveUser(
                "EMP001", "Ilboudo", "Jean-Claude", "jean_claude.ilboudo@entreprise.fr",
                ContractType.APPRENTICESHIP, Grade.SENIOR, Role.CHEF_PROJET,
                informatique, chefProjet, "4000.00", "0612345678"
        );

        User saad = saveUser(
                "EMP002", "Tarmidi", "Saad", "saad.tarmidi@entreprise.fr",
                ContractType.PERMANENT_FULL_TIME, Grade.JUNIOR, Role.EMPLOYE,
                rh, devBackend, "3000.00", "0698765432"
        );

        User adam = saveUser(
                "EMP003", "Swiczka", "Adam", "adam.swiczka@entreprise.fr",
                ContractType.FIXED_TERM_FULL_TIME, Grade.EXPERT, Role.CHEF_DEPARTEMENT,
                finance, chefDepartement, "5000000.00", "0678901234"
        );

        User haitam = saveUser(
                "EMP004", "Hania", "Haitam", "haitam.hania@entreprise.fr",
                ContractType.PERMANENT_FULL_TIME, Grade.SENIOR, Role.ADMINISTRATEUR,
                informatique, adminSystem, "4500.00", "0654321098"
        );

        User medhi = saveUser(
                "EMP005", "Nom", "Medhi", "medhi.nom@entreprise.fr",
                ContractType.PERMANENT_PART_TIME, Grade.JUNIOR, Role.EMPLOYE,
                finance, devFrontend, "2800.00", "0643210987"
        );

        User chefSup = saveUser(
                "EMP006", "Anonyme1", "CDP", "cdp@entreprise.fr",
                ContractType.TEMPORARY_AGENCY, Grade.EXPERT, Role.CHEF_PROJET,
                finance, chefProjet, "6000.00", "0600000000"
        );

        User chefRH = saveUser(
                "EMP007", "Anonyme2", "RespoRH", "respoRh@entreprise.fr",
                ContractType.TEMPORARY_AGENCY, Grade.SENIOR, Role.CHEF_DEPARTEMENT,
                rh, responsableRH, "15000.00", "0624194672"
        );

        // ==========================
        // PROJETS
        // ==========================
        Project siteWeb = projectRepository.save(
                new Project("Refonte Site Web", jean_claude, "Refonte complète du site", Status.IN_PROGRESS)
        );
        Project mobileApp = projectRepository.save(
                new Project("Application Mobile", haitam, "App mobile interne", Status.PLANNED)
        );
        Project cloudMigration = projectRepository.save(
                new Project("Migration Cloud", jean_claude, "Migration AWS", Status.IN_PROGRESS)
        );

        // Assignations
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
        // FICHES DE PAIE
        // ==========================
        payslipRepository.save(new Payslip(2023, 11, new BigDecimal("500.00"), new BigDecimal("1000.00"), jean_claude));
        payslipRepository.save(new Payslip(2024, 10, new BigDecimal("200.00"), new BigDecimal("600.00"), saad));
        payslipRepository.save(new Payslip(2024, 10, new BigDecimal("1000.00"), new BigDecimal("1400.00"), adam));
        payslipRepository.save(new Payslip(2023, 12, new BigDecimal("300.00"), new BigDecimal("800.00"), haitam));
        payslipRepository.save(new Payslip(2024, 9, new BigDecimal("250.00"), new BigDecimal("650.00"), medhi));
        payslipRepository.save(new Payslip(2024, 8, new BigDecimal("150.00"), new BigDecimal("350.00"), medhi));

        System.out.println("🎉 Base initialisée avec succès !");
    }

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
        u.setPassword(passwordEncoder.encode("motdepasse123"));
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
