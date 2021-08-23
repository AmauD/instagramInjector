package com.instagram.injector.service;


import com.instagram.injector.entity.Instagram;
import com.instagram.injector.repository.InstagramRepository;
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
