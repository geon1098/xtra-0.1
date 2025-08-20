package com.mysite.xtra.guide;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuidePostService {
    private final GuidePostRepository repository;

    public List<GuidePost> findAll() {
        return repository.findAll();
    }

    public GuidePost getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    public GuidePost create(String title, String content, String author) {
        GuidePost post = new GuidePost();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(author);
        return repository.save(post);
    }

    public GuidePost update(Long id, String title, String content) {
        GuidePost post = getById(id);
        post.setTitle(title);
        post.setContent(content);
        return repository.save(post);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}


