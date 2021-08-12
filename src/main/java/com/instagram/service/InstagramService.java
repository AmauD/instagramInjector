package com.instagram.service;


import com.instagram.entity.Instagram;
import com.instagram.repository.InstagramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InstagramService {
    @Autowired
    InstagramRepository instagramRepository;

    public Instagram create(Instagram instagram) {
        return instagramRepository.save(instagram);
    }
}
