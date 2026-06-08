package vn.thinher.quickjob.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.thinher.quickjob.domain.User;
import vn.thinher.quickjob.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public List<User> handleFetchAllUsers() {
        return this.userRepository.findAll();
    }

    public User handleFetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }
    public User handleUpdateUser(User user) {
        User existingUser = this.handleFetchUserById(user.getId());
        if (existingUser != null) {
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setPassword(user.getPassword());
            existingUser = this.userRepository.save(existingUser);
        }
        return existingUser;
    }
    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }
}
