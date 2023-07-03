package com.kbbukopin.webdash.enums;

public enum ProjectType {
    IN_HOUSE("In House"),
    IN_HOUSE_LINTAS_UNIT("In House - Lintas Unit"),
    OUTSOURCE("Outsource"),
    JOIN_DEV("Join Dev");

    private final String name;

    private ProjectType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
