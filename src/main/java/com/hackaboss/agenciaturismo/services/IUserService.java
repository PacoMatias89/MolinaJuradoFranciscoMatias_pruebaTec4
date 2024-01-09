package com.hackaboss.agenciaturismo.services;

import com.hackaboss.agenciaturismo.model.User;
import org.springframework.stereotype.Service;

@Service
public interface IUserService {

    User createUser(User user);
    User findByEmail(String email);
}
