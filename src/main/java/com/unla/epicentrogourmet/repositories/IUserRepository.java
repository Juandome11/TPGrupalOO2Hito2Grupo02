package com.unla.epicentrogourmet.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.unla.epicentrogourmet.entities.User;

@Repository("userRepository")
public interface IUserRepository extends JpaRepository<User, Long> {

	boolean existsByUsername(String username);

	@Query("SELECT u FROM User u JOIN FETCH u.userRoles WHERE u.username = :username")
	Optional<User> findByUsernameAndFetchUserRolesEagerly(String username);
}
