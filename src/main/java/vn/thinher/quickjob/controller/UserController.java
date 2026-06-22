package vn.thinher.quickjob.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.thinher.quickjob.domain.User;
import vn.thinher.quickjob.domain.dto.ResCreateUserDTO;
import vn.thinher.quickjob.domain.dto.ResGetUserDTO;
import vn.thinher.quickjob.domain.dto.ResultPaginationDTO;
import vn.thinher.quickjob.service.UserService;
import vn.thinher.quickjob.util.annotation.ApiMessage;
import vn.thinher.quickjob.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(
            @Filter Specification<User> specification,
            Pageable pageable) {
        ResultPaginationDTO result = this.userService.handleFetchAllUsers(specification, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Fetch user by id")
    public ResponseEntity<ResGetUserDTO> getUser(@PathVariable("id") long id) throws IdInvalidException {
        User user = userService.handleFetchUserById(id);
        if (user == null) {
            throw new IdInvalidException("User not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResGetUserDTO(user));
    }

    @PostMapping("/users")
    @ApiMessage("Create new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isEmailExist = userService.isEmailExist(user.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("Email already exist");
        }

        String hashedPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        User createdUser = userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(createdUser));
    }

    @PutMapping("/users")
    @ApiMessage("Update user")
    public ResponseEntity<User> updateUser(@RequestBody User user) throws IdInvalidException {
        User existingUser = userService.handleUpdateUser(user);
        if (existingUser == null) {
            throw new IdInvalidException("User not found");
        }
        return ResponseEntity.ok(existingUser);
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        User user = userService.handleFetchUserById(id);
        if (user == null) {
            throw new IdInvalidException("User not found");
        }
        userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }
}
