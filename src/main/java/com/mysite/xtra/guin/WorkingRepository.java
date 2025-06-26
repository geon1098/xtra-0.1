package com.mysite.xtra.guin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mysite.xtra.user.SiteUser;

public interface WorkingRepository extends JpaRepository<Working, Long>{

	Page<Working> findAll(Pageable pageable);

	@Query("select "
			+ "distinct w "
			+ "from Working w " 
			+ "left outer join SiteUser u1 on w.author=u1 "
			+ "where "
			+ "   w.title like %:kw% "
			+ "   or w.siteName like %:kw% "
			+ "   or w.jobDescription like %:kw% "
			+ "   or u1.username like %:kw% ")
	Page<Working> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
	
	// 사용자별 구인글 개수
	long countByAuthor(SiteUser author);
	
	// 사용자별 구인글 목록 (최신순)
	Page<Working> findByAuthorOrderByCreateDateDesc(SiteUser author, Pageable pageable);
	
	// 사용자별 최근 구인글 5개
	List<Working> findTop5ByAuthorOrderByCreateDateDesc(SiteUser author);
}
