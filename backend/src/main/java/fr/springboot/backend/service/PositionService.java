package fr.springboot.backend.service;

import fr.springboot.backend.model.Position;
import fr.springboot.backend.model.User;
import fr.springboot.backend.repository.PositionRepository;
import fr.springboot.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PositionService {

    private final PositionRepository positionRepository;
    private final UserRepository userRepository;

    public PositionService(PositionRepository positionRepository, UserRepository userRepository) {
        this.positionRepository = positionRepository;
        this.userRepository = userRepository;
    }

    // ------------------ CRUD ----------------------

    public List<Position> findAll() {
        return positionRepository.findAll();
    }

    public Optional<Position> findById(Integer id) {
        return positionRepository.findById(id);
    }

    public Optional<Position> findByName(String name) {
        return positionRepository.findByNameIgnoreCase(name);
    }

    public Position save(Position position) {
        return positionRepository.save(position);
    }

    public void delete(Integer id) {
        positionRepository.deleteById(id);
    }

    // ------------------ RELATIONS ----------------------

    public List<User> getUsersOfPosition(Integer positionId) {
        return positionRepository.findUsersByPosition(positionId);
    }

    public long countUsers(Integer positionId) {
        return positionRepository.countUsersByPosition(positionId);
    }

    // ------------------ ASSIGNATION ----------------------

    public boolean assignUser(Integer positionId, String matricule) {
        Optional<Position> pos = positionRepository.findById(positionId);
        Optional<User> user = userRepository.findByMatricule(matricule);

        if (pos.isEmpty() || user.isEmpty()) return false;

        User u = user.get();
        u.setPosition(pos.get());
        userRepository.save(u);
        return true;
    }

    public boolean removeUser(Integer positionId, String matricule) {
        Optional<User> user = userRepository.findByMatricule(matricule);

        if (user.isEmpty()) return false;

        User u = user.get();

        if (u.getPosition() == null || !u.getPosition().getId().equals(positionId))
            return false;

        u.setPosition(null);
        userRepository.save(u);
        return true;
    }
}
