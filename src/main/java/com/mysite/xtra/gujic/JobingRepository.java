package com.mysite.xtra.gujic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobingRepository extends JpaRepository<Jobing, Long>{
//리포지터리에 추상메서드 작성한다
	//게시글 모음에 보일 속성만 파라미터로 놔둔다 리스트로 
	Page<Jobing> findAll(Pageable pageable);
}
