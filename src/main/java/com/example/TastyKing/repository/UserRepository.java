package com.example.TastyKing.repository;

import com.example.TastyKing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByUserName(String username);

    List<User> findByRole(String roles);
}
