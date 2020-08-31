package com.kakaopay.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kakaopay.model.Token;

public interface TokenJpaRepo extends JpaRepository<Token, String> {

}
