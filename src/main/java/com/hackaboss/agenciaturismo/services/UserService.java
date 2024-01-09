package com.hackaboss.agenciaturismo.services;

import com.hackaboss.agenciaturismo.model.User;
import com.hackaboss.agenciaturismo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public User createUser(User user) {

        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


}
