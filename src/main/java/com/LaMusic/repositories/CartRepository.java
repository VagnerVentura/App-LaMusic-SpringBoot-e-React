package com.LaMusic.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.LaMusic.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, UUID> {

	Optional<Cart> findByUserId(UUID userId);
	
}
