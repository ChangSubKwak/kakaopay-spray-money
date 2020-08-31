package com.kakaopay.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kakaopay.model.User;

public interface UserJpaRepo extends JpaRepository<User, String> {
}
