package com.LaMusic.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.LaMusic.dto.InactiveCustomerDTO;
import com.LaMusic.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {
	Optional<User> findByEmail(String email);
	
	@Query("""
		    SELECT new com.LaMusic.dto.InactiveCustomerDTO(
		        u.id, u.name, u.email, MAX(o.orderDate)
		    )
		    FROM User u
		    LEFT JOIN Order o ON o.user.id = u.id
		    GROUP BY u.id, u.name, u.email
		    HAVING MAX(o.orderDate) IS NULL OR MAX(o.orderDate) < :cutoff
		""")
		List<InactiveCustomerDTO> findInactiveCustomers(@Param("cutoff") LocalDate cutoff);
	
}
