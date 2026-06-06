package vn.thinher.quickjob.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import vn.thinher.quickjob.domain.User;
import vn.thinher.quickjob.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public String createNewUser(@RequestBody User postManUser) {
        userService.handleCreateUser(postManUser);
        return "User created successfully";
    }
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        userService.handleDeleteUser(id);
        return "User deleted successfully";
    }
}
