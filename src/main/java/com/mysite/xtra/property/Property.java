package com.mysite.xtra.property;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Property {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Column(length = 2000)
    private String description;
    private String author;
    private String phone;
    private LocalDate createdAt;

    @ElementCollection
    private List<String> imageUrls; // 이미지 파일명 또는 URL 리스트
} 