package com.online.judge.auth.controllers;

import com.online.judge.user.entities.User;
import com.online.judge.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    private ResponseEntity<String> signUp(@Valid @RequestBody User user) {
        if(userRepository.findByEmailId(user.getEmailId()).isPresent()) {
            ResponseEntity.status(HttpStatus.CONFLICT).
                    body("EmailId: " + user.getEmailId() + " already exist");
        }
        if(userRepository.findById(user.getUserName()).isPresent()) {
            ResponseEntity.status(HttpStatus.CONFLICT).
                    body("Username: " + user.getUserName() + " already exist");
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("user successfully signed up");
    }
}
