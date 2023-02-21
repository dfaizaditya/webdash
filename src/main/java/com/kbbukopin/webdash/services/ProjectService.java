package com.kbbukopin.webdash.services;

import com.kbbukopin.webdash.dto.PagedResponse;
import com.kbbukopin.webdash.dto.ProjectResponse;
import com.kbbukopin.webdash.entity.Project;

public interface ProjectService {

    PagedResponse<Project> getAllProjects(int page, int size);
    
}
