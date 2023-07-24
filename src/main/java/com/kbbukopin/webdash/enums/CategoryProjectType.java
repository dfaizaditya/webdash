package com.kbbukopin.webdash.enums;

public enum CategoryProjectType {
    BUSINESS("Business"),
    OPERATIONAL("Operational"),
    INITIATIVE("Initiative"),
    REGULATION("Regulation"),
    NA("N/A");

    private final String name;

    private CategoryProjectType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
