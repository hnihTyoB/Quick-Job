package vn.thinher.quickjob.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.thinher.quickjob.service.error.IdInvalidException;

@RestController
public class HelloController {

    @GetMapping("/")
    public String getHelloWorld() throws IdInvalidException {
        if (true) {
            throw new IdInvalidException("This is an exception");
        }
        return "Hello World Thinher";
    }
}
