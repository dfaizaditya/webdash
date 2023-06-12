package com.kbbukopin.webdash.enums;

public enum StatusOptions2 {
    ON_SCHEDULE("On Schedule"),
    ON_TIME("On time"),
    AHEAD("Ahead"),
    OVERDUE("Overdue"),
    NA("N/A");

    private final String name;

    private StatusOptions2(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
