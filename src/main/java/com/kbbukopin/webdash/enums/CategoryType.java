package com.kbbukopin.webdash.enums;

public enum CategoryType {
    INSIDEN("Insiden"),
    PROYEK("Proyek");

    private final String name;

    private CategoryType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

