package com.example.muaring.domain.test.repository;

import com.example.muaring.domain.test.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Test, Long> {
}