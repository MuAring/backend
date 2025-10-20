package com.example.muaring.domain.group.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "group_category_mapping")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupCategoryMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_category_id", nullable = false)
    private GroupCategory groupCategory;
}
