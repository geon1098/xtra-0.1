package com.mysite.xtra.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<SiteUser, Long>{
	Optional<SiteUser> findByUsername(String username);
	Optional<SiteUser> findByEmail(String email);
	Optional<SiteUser> findByUsernameAndEmail(String username, String email);
}
