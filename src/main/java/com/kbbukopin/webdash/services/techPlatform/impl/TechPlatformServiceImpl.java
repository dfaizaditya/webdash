package com.kbbukopin.webdash.services.techPlatform.impl;

import com.kbbukopin.webdash.entity.TechPlatform;
import com.kbbukopin.webdash.repository.TechPlatformRepository;
import com.kbbukopin.webdash.services.techPlatform.TechPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TechPlatformServiceImpl implements TechPlatformService {

    @Autowired
    private TechPlatformRepository techPlatformRepository;

    @Override
    public List<TechPlatform> getAllTechPlatforms() {
        return techPlatformRepository.findAll();
    }
}
