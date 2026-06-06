package vn.thinher.quickjob.service;

import org.springframework.stereotype.Service;

import vn.thinher.quickjob.domain.User;
import vn.thinher.quickjob.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }
    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }
}
