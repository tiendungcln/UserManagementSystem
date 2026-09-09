package com.library.usermanagementsystem.repository;

import com.library.usermanagementsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName);

    List<User> findByFullNameContainingIgnoreCase(String fullName);

}