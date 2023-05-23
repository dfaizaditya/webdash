package com.kbbukopin.webdash.enums;

public enum ProjectType {
    IN_HOUSE("In House"),
    OUTSOURCE("Outsource"),
    IN_HOUSE_LINTAS_UNIT("In House - Lintas Unit");

    private final String name;

    private ProjectType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
