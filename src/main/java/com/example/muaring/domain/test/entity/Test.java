package com.example.muaring.domain.test.entity;

import com.example.muaring.common.response.ErrorCode;
import com.example.muaring.domain.common.BaseTimeEntity;
import com.example.muaring.domain.test.exception.TestException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "test")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 엔티티에서는 @Setter, @AllArgsConstructor, @ToString 사용 지양, builder 패턴 추천
@SQLDelete(sql = "UPDATE test SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Test extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Builder
    private Test(String content) {
        this.content = content;
    }

    public void updateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new TestException(ErrorCode.INVALID_TEST_CONTENT);
        }
        this.content = content;
    }
}