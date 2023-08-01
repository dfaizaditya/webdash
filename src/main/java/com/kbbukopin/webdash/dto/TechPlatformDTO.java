package com.kbbukopin.webdash.dto;

public class TechPlatformDTO {
    private String name;
    private Long projectCount;

    // Constructor
    public TechPlatformDTO(String name, Long projectCount) {
        this.name = name;
        this.projectCount = projectCount;
    }

    // Getters and setters (you can use Lombok to avoid writing boilerplate code)

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Long projectCount) {
        this.projectCount = projectCount;
    }
}

