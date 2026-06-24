package vn.thinher.quickjob.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.thinher.quickjob.domain.User;
import vn.thinher.quickjob.domain.dto.Meta;
import vn.thinher.quickjob.domain.dto.ResCreateUserDTO;
import vn.thinher.quickjob.domain.dto.ResGetUserDTO;
import vn.thinher.quickjob.domain.dto.ResUpdateUserDTO;
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

        // Remove sensitive data
        List<ResGetUserDTO> listUser = userPage.getContent().stream().map(this::convertToResGetUserDTO).toList();
        result.setResult(listUser);
        return result;
    }

    public User handleFetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public ResGetUserDTO convertToResGetUserDTO(User user) {
        ResGetUserDTO resGetUserDTO = new ResGetUserDTO();
        resGetUserDTO.setId(user.getId());
        resGetUserDTO.setName(user.getName());
        resGetUserDTO.setEmail(user.getEmail());
        resGetUserDTO.setAge(user.getAge());
        resGetUserDTO.setGender(user.getGender());
        resGetUserDTO.setAddress(user.getAddress());
        resGetUserDTO.setCreatedAt(user.getCreatedAt());
        resGetUserDTO.setUpdatedAt(user.getUpdatedAt());
        return resGetUserDTO;
    }

    public User handleFetchUserByEmail(String email) {
        Optional<User> userOptional = Optional.ofNullable(this.userRepository.findByEmail(email));
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO resCreateUserDTO = new ResCreateUserDTO();
        resCreateUserDTO.setId(user.getId());
        resCreateUserDTO.setName(user.getName());
        resCreateUserDTO.setEmail(user.getEmail());
        resCreateUserDTO.setAge(user.getAge());
        resCreateUserDTO.setGender(user.getGender());
        resCreateUserDTO.setAddress(user.getAddress());
        resCreateUserDTO.setCreatedAt(user.getCreatedAt());
        return resCreateUserDTO;
    }

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }

    public User handleUpdateUser(User user) {
        User existingUser = this.handleFetchUserById(user.getId());
        if (existingUser != null) {
            existingUser.setName(user.getName());
            existingUser.setAge(user.getAge());
            existingUser.setGender(user.getGender());
            existingUser.setAddress(user.getAddress());
            existingUser = this.userRepository.save(existingUser);
        }
        return existingUser;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO resUpdateUserDTO = new ResUpdateUserDTO();
        resUpdateUserDTO.setId(user.getId());
        resUpdateUserDTO.setName(user.getName());
        resUpdateUserDTO.setAge(user.getAge());
        resUpdateUserDTO.setGender(user.getGender());
        resUpdateUserDTO.setAddress(user.getAddress());
        resUpdateUserDTO.setUpdatedAt(user.getUpdatedAt());
        return resUpdateUserDTO;
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public void updateUserToken(String email, String token) {
        User user = this.handleFetchUserByEmail(email);
        if (user != null) {
            user.setRefreshToken(token);
            this.userRepository.save(user);
        }
    }

    public User getUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }

    public void deleteUserToken(String refreshToken) {
        User user = this.getUserByRefreshTokenAndEmail(refreshToken, null);
        if (user != null) {
            user.setRefreshToken(null);
            this.userRepository.save(user);
        }
    }
}
