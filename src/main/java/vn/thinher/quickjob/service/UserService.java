package vn.thinher.quickjob.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.thinher.quickjob.domain.User;
import vn.thinher.quickjob.domain.dto.Meta;
import vn.thinher.quickjob.domain.dto.ResultPaginationDTO;
import vn.thinher.quickjob.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResultPaginationDTO handleFetchAllUsers(Specification<User> specification, Pageable pageable) {
        Page<User> userPage = this.userRepository.findAll(specification, pageable);
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(userPage.getTotalPages());
        meta.setTotal(userPage.getTotalElements());
        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(userPage.getContent());
        return result;
    }

    public User handleFetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public User handleFetchUserByEmail(String email) {
        Optional<User> userOptional = Optional.ofNullable(this.userRepository.findByEmail(email));
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
