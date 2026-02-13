package org.example.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.userservice.model.User;
import org.example.userservice.repository.UserRepository;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    @PostMapping
    public User createUser(@RequestBody User user){
        return userRepository.save(user);
    }
    @GetMapping
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

}
