package vn.thinher.quickjob.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import vn.thinher.quickjob.domain.User;
import vn.thinher.quickjob.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.handleFetchAllUsers();
    }
    @GetMapping("/users/{id}")
    public User getUser(@PathVariable("id") long id) {
        return userService.handleFetchUserById(id);
    }

    @PostMapping("/users")
    public String createNewUser(@RequestBody User user) {
        userService.handleCreateUser(user);
        return "User created successfully";
        }
    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        User existingUser = userService.handleUpdateUser(user);
        return existingUser;
    }
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        userService.handleDeleteUser(id);
        return "User deleted successfully";
    }
}
