package com.instagram.injector.repository;

import com.instagram.injector.entity.Instagram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstagramRepository extends JpaRepository<Instagram, Long> {
    Boolean existsByPostId(Long id);
}
