package com.kbbukopin.webdash.services.pic.impl;

import com.kbbukopin.webdash.entity.Pic;
import com.kbbukopin.webdash.repository.PicRepository;
import com.kbbukopin.webdash.services.pic.PicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PicServiceImpl implements PicService {

    @Autowired
    private PicRepository picRepository;

    @Override
    public List<Pic> getAllPics() {
        return picRepository.findAll();
    }
}
