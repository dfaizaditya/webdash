package com.kbbukopin.webdash.services.userSponsor.impl;

import com.kbbukopin.webdash.entity.UserSponsor;
import com.kbbukopin.webdash.repository.UserSponsorRepository;
import com.kbbukopin.webdash.services.userSponsor.UserSponsorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserSponsorServiceImpl implements UserSponsorService {

    @Autowired
    private UserSponsorRepository userSponsorRepository;

    @Override
    public List<UserSponsor> getAllUserSponsors() {
        return userSponsorRepository.findAll();
    }
}
