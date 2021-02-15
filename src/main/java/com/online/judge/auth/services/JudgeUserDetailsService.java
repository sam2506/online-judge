package com.online.judge.auth.services;

import com.online.judge.user.entities.User;
import com.online.judge.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JudgeUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findById(userName);
        if(user.isPresent()) {
            return new JudgeUserDetails(user.get());
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
