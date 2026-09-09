package com.library.usermanagementsystem.service;

import com.library.usermanagementsystem.dto.LoginRequest;
import com.library.usermanagementsystem.dto.UserRequest;
import com.library.usermanagementsystem.dto.UserResponse;
import com.library.usermanagementsystem.entity.User;
import com.library.usermanagementsystem.exception.UserNotFoundException;
import com.library.usermanagementsystem.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<UserResponse> getAllUsers(Pageable pageable){
        Page<User> users = userRepository.findAll(pageable);

        return users.map(user -> {

            UserResponse response = new UserResponse();

            response.setUserId(user.getUserId());
            response.setFullName(user.getFullName());
            response.setUserName(user.getUserName());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole());
            response.setCreatedAt(user.getCreatedAt());
            response.setUpdatedAt(user.getUpdatedAt());

            return response;
        });
    }

    public UserResponse getUserById(Long id){
        User user = userRepository.findById(id).orElse(null);

        if (user == null){
            throw new UserNotFoundException("User not found with id: " + id);
        }

        UserResponse response = new UserResponse();

        response.setUserId(user.getUserId());
        response.setFullName(user.getFullName());
        response.setUserName(user.getUserName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }

    public UserResponse createUser(UserRequest request){
        User user = new User();

        user.setFullName(request.getFullName());
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        UserResponse response = new UserResponse();

        response.setUserId(savedUser.getUserId());
        response.setFullName(savedUser.getFullName());
        response.setUserName(savedUser.getUserName());
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole());
        response.setCreatedAt(savedUser.getCreatedAt());
        response.setUpdatedAt(savedUser.getUpdatedAt());

        return response;
    }

    public UserResponse updateUser(Long id, UserRequest request){
        User existingUser = userRepository.findById(id).orElse(null);

        if (existingUser == null){
            throw new UserNotFoundException("User not found with id: " + id);
        }

        existingUser.setFullName(request.getFullName());
        existingUser.setUserName(request.getUserName());
        existingUser.setEmail(request.getEmail());
        existingUser.setPassword(request.getPassword());

        User savedUser = userRepository.save(existingUser);

        UserResponse response = new UserResponse();

        response.setUserId(savedUser.getUserId());
        response.setFullName(savedUser.getFullName());
        response.setUserName(savedUser.getUserName());
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole());
        response.setCreatedAt(savedUser.getCreatedAt());
        response.setUpdatedAt(savedUser.getUpdatedAt());

        return response;
    }

    public void deleteUser(Long id){
        if (!userRepository.existsById(id)){
            throw new UserNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
    }

    public List<UserResponse> searchByFullName(String fullName) {

        List<User> users = userRepository.findByFullNameContainingIgnoreCase(fullName);

        if (users.isEmpty()) {
            throw new UserNotFoundException("User not found with full name: " + fullName);
        }

        return users.stream().map(user -> {

            UserResponse response = new UserResponse();

            response.setUserId(user.getUserId());
            response.setFullName(user.getFullName());
            response.setUserName(user.getUserName());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole());
            response.setCreatedAt(user.getCreatedAt());
            response.setUpdatedAt(user.getUpdatedAt());

            return response;

        }).toList();

    }

    public boolean login(LoginRequest request){

        User user = userRepository.findByUserName(request.getUserName()).orElse(null);

        if (user == null){
            return false;
        }

        return passwordEncoder.matches(request.getPassword(), user.getPassword());

    }

}

