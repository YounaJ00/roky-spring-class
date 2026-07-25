package com.example.springboot.test.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataTestRepository extends JpaRepository<TestJpaEntity, Long> {}
