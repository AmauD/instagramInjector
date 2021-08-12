package com.instagram.repository;

import com.instagram.entity.Instagram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstagramRepository extends JpaRepository<Instagram, Long> {
}
