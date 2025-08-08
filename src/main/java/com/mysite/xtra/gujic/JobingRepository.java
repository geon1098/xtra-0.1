package com.mysite.xtra.gujic;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import com.mysite.xtra.user.SiteUser;

public interface JobingRepository extends JpaRepository<Jobing, Long>{
//리포지터리에 추상메서드 작성한다
	//게시글 모음에 보일 속성만 파라미터로 놔둔다 리스트로 
	Page<Jobing> findAll(Pageable pageable);
	
	// 사용자별 구직글 개수
	long countByAuthor(SiteUser author);
	
	// 사용자별 구직글 목록 (최신순)
	Page<Jobing> findByAuthorOrderByCreateDateDesc(SiteUser author, Pageable pageable);
	
	// 사용자별 최근 구직글 5개
	List<Jobing> findTop5ByAuthorOrderByCreateDateDesc(SiteUser author);
	
	// 키워드 검색 (제목, 자기소개, 희망업무, 희망지역)
	Page<Jobing> findByNameContainingIgnoreCaseOrIntroductionContainingIgnoreCaseOrRequestWorkContainingIgnoreCaseOrHopAreaContainingIgnoreCase(String name, String intro, String requestWork, String hopArea, Pageable pageable);

	@Query("select j from Jobing j where ( (:kw is null or :kw = '') or j.name like %:kw% or j.introduction like %:kw% or j.requestWork like %:kw% or j.hopArea like %:kw% ) "
			+ "and ( :career is null or :career = '' or j.career like %:career% ) "
			+ "and ( :age is null or :age = '' or j.age like %:age% ) "
			+ "and ( :gender is null or :gender = '' or j.gender like %:gender% )")
	Page<Jobing> findAllByFilters(@Param("kw") String kw,
			@Param("career") String career,
			@Param("age") String age,
			@Param("gender") String gender,
			Pageable pageable);
	
	// 사용자별 구직글 삭제
	@Modifying
	@Query("DELETE FROM Jobing j WHERE j.author = :author")
	void deleteByAuthor(@Param("author") SiteUser author);
}
