package com.mysite.xtra.guin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkingRepository extends JpaRepository<Working, Long>{

	Page<Working> findAll(Pageable pageable);
}
