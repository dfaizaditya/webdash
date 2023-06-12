package com.kbbukopin.webdash.enums;

public enum StatusOptions1 {
    FINISHED("Finished"),
    ON_GOING("On Going"),
    DITUNDA("Ditunda");

    private final String name;

    private StatusOptions1(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
