package fr.springboot.backend;

import fr.springboot.backend.enums.ContractType;
import fr.springboot.backend.enums.Grade;
import fr.springboot.backend.enums.Role;
import fr.springboot.backend.model.Department;
import fr.springboot.backend.model.Position;
import fr.springboot.backend.model.User;
import fr.springboot.backend.repository.DepartmentRepository;
import fr.springboot.backend.repository.PositionRepository;
import fr.springboot.backend.repository.UserRepository;
import fr.springboot.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {

        // Vérifier si des utilisateurs existent déjà
        if (userRepository.count() > 0) {
            System.out.println("✅ Des utilisateurs existent déjà dans la base de données.");
            return;
        }

        System.out.println("🚀 Initialisation des données de test...");

        // ✅ CRÉER LES DÉPARTEMENTS AVEC LE CHAMP CODE
        Department it = departmentRepository.findById(1).orElseGet(() -> {
            Department dept = new Department();
            dept.setCode("IT");  // ✅ AJOUT DU CODE
            dept.setName("Informatique");
            return departmentRepository.save(dept);
        });

        Department rh = departmentRepository.findById(2).orElseGet(() -> {
            Department dept = new Department();
            dept.setCode("RH");  // ✅ AJOUT DU CODE
            dept.setName("Ressources Humaines");
            return departmentRepository.save(dept);
        });

        Department finance = departmentRepository.findById(3).orElseGet(() -> {
            Department dept = new Department();
            dept.setCode("FIN");  // ✅ AJOUT DU CODE
            dept.setName("Finance");
            return departmentRepository.save(dept);
        });

        // Créer les positions
        Position dev = positionRepository.findById(1).orElseGet(() -> {
            Position pos = new Position();
            pos.setName("Développeur");
            return positionRepository.save(pos);
        });

        Position manager = positionRepository.findById(2).orElseGet(() -> {
            Position pos = new Position();
            pos.setName("Manager");
            return positionRepository.save(pos);
        });

        Position analyst = positionRepository.findById(3).orElseGet(() -> {
            Position pos = new Position();
            pos.setName("Analyste");
            return positionRepository.save(pos);
        });

        // ✅ CRÉER LES 7 UTILISATEURS AVEC MATRICULES GÉNÉRÉS

        // 1. ADMINISTRATEUR
        User admin = new User();
        admin.setMatricule(userService.generateMatricule());
        admin.setLastName("Admin");
        admin.setFirstName("Super");
        admin.setEmail("admin@entreprise.fr");
        admin.setPassword(passwordEncoder.encode("motdepasse123"));
        admin.setPhone("0601020304");
        admin.setAddress("123 Rue Principale, Paris");
        admin.setGrade(Grade.SENIOR);
        admin.setRole(Role.ADMINISTRATEUR);
        admin.setContractType(ContractType.PERMANENT_FULL_TIME);
        admin.setBaseSalary(new BigDecimal("5000.00"));
        admin.setDepartment(it);
        admin.setPosition(manager);
        userRepository.save(admin);

        // 2. EMPLOYÉ IT
        User employe = new User();
        employe.setMatricule(userService.generateMatricule());
        employe.setLastName("Dupont");
        employe.setFirstName("Jean");
        employe.setEmail("jean.dupont@entreprise.fr");
        employe.setPassword(passwordEncoder.encode("motdepasse123"));
        employe.setPhone("0601020305");
        employe.setAddress("456 Avenue de la République, Lyon");
        employe.setGrade(Grade.JUNIOR);
        employe.setRole(Role.EMPLOYE);
        employe.setContractType(ContractType.PERMANENT_FULL_TIME);
        employe.setBaseSalary(new BigDecimal("2500.00"));
        employe.setDepartment(it);
        employe.setPosition(dev);
        userRepository.save(employe);

        // 3. CHEF DE PROJET IT
        User chefProjet = new User();
        chefProjet.setMatricule(userService.generateMatricule());
        chefProjet.setLastName("Martin");
        chefProjet.setFirstName("Sophie");
        chefProjet.setEmail("sophie.martin@entreprise.fr");
        chefProjet.setPassword(passwordEncoder.encode("motdepasse123"));
        chefProjet.setPhone("0601020306");
        chefProjet.setAddress("789 Boulevard des Champs, Marseille");
        chefProjet.setGrade(Grade.SENIOR);
        chefProjet.setRole(Role.CHEF_PROJET);
        chefProjet.setContractType(ContractType.PERMANENT_FULL_TIME);
        chefProjet.setBaseSalary(new BigDecimal("4000.00"));
        chefProjet.setDepartment(it);
        chefProjet.setPosition(manager);
        userRepository.save(chefProjet);

        // 4. CHEF DÉPARTEMENT IT (ADMINISTRATEUR - Haitam)
        User chefDept = new User();
        chefDept.setMatricule(userService.generateMatricule());
        chefDept.setLastName("Bernard");
        chefDept.setFirstName("Haitam");
        chefDept.setEmail("haitam.bernard@entreprise.fr");
        chefDept.setPassword(passwordEncoder.encode("motdepasse123"));
        chefDept.setPhone("0601020307");
        chefDept.setAddress("321 Rue de la Liberté, Toulouse");
        chefDept.setGrade(Grade.EXPERT);
        chefDept.setRole(Role.ADMINISTRATEUR);  // ✅ ADMINISTRATEUR pour les tests
        chefDept.setContractType(ContractType.PERMANENT_FULL_TIME);
        chefDept.setBaseSalary(new BigDecimal("6000.00"));
        chefDept.setDepartment(it);
        chefDept.setPosition(manager);
        userRepository.save(chefDept);

        // 5. EMPLOYÉ RH
        User employeRH = new User();
        employeRH.setMatricule(userService.generateMatricule());
        employeRH.setLastName("Petit");
        employeRH.setFirstName("Claire");
        employeRH.setEmail("claire.petit@entreprise.fr");
        employeRH.setPassword(passwordEncoder.encode("motdepasse123"));
        employeRH.setPhone("0601020308");
        employeRH.setAddress("654 Avenue Victor Hugo, Nantes");
        employeRH.setGrade(Grade.INTERMEDIAIRE);
        employeRH.setRole(Role.EMPLOYE);
        employeRH.setContractType(ContractType.PERMANENT_FULL_TIME);
        employeRH.setBaseSalary(new BigDecimal("3000.00"));
        employeRH.setDepartment(rh);
        employeRH.setPosition(analyst);
        userRepository.save(employeRH);

        // 6. CHEF DÉPARTEMENT RH
        User chefDeptRH = new User();
        chefDeptRH.setMatricule(userService.generateMatricule());
        chefDeptRH.setLastName("Moreau");
        chefDeptRH.setFirstName("Thomas");
        chefDeptRH.setEmail("thomas.moreau@entreprise.fr");
        chefDeptRH.setPassword(passwordEncoder.encode("motdepasse123"));
        chefDeptRH.setPhone("0601020309");
        chefDeptRH.setAddress("987 Rue Jean Jaurès, Strasbourg");
        chefDeptRH.setGrade(Grade.SENIOR);
        chefDeptRH.setRole(Role.CHEF_DEPARTEMENT);
        chefDeptRH.setContractType(ContractType.PERMANENT_FULL_TIME);
        chefDeptRH.setBaseSalary(new BigDecimal("5500.00"));
        chefDeptRH.setDepartment(rh);
        chefDeptRH.setPosition(manager);
        userRepository.save(chefDeptRH);

        // 7. EMPLOYÉ FINANCE
        User employeFinance = new User();
        employeFinance.setMatricule(userService.generateMatricule());
        employeFinance.setLastName("Laurent");
        employeFinance.setFirstName("Emma");
        employeFinance.setEmail("emma.laurent@entreprise.fr");
        employeFinance.setPassword(passwordEncoder.encode("motdepasse123"));
        employeFinance.setPhone("0601020310");
        employeFinance.setAddress("147 Boulevard Gambetta, Bordeaux");
        employeFinance.setGrade(Grade.JUNIOR);
        employeFinance.setRole(Role.EMPLOYE);
        employeFinance.setContractType(ContractType.PERMANENT_FULL_TIME);
        employeFinance.setBaseSalary(new BigDecimal("2800.00"));
        employeFinance.setDepartment(finance);
        employeFinance.setPosition(analyst);
        userRepository.save(employeFinance);

        System.out.println(" 7 utilisateurs de test créés avec succès !");
        System.out.println(" Matricules générés :");
        userRepository.findAll().forEach(u ->
                System.out.println("   - " + u.getMatricule() + " : " + u.getFullName() + " (" + u.getRole() + ")")
        );
    }
}
