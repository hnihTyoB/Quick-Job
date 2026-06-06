package vn.thinher.quickjob.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.thinher.quickjob.domain.User;
import vn.thinher.quickjob.service.UserService;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/create")
    public void createNewUser() {
        User user = new User();
        user.setName("Thinher");
        user.setEmail("nctmdt@gmail.com");
        user.setPassword("123456");
        userService.handleCreateUser(user);
    }
}
