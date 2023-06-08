package com.kbbukopin.webdash.services.appPlatform.impl;

import com.kbbukopin.webdash.entity.AppPlatform;
import com.kbbukopin.webdash.repository.AppPlatformRepository;
import com.kbbukopin.webdash.services.appPlatform.AppPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppPlatformServiceImpl implements AppPlatformService {

    @Autowired
    private AppPlatformRepository appPlatformRepository;

    @Override
    public List<AppPlatform> getAllAppPlatforms() {
        return appPlatformRepository.findAll();
    }
}
