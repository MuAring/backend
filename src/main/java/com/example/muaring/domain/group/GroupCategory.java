package com.example.muaring.domain.group;

import com.example.muaring.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "group_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_category_id")
    private Long groupCategoryId;

    @Column(length = 30, nullable = false, unique = true)
    private String name;
}
