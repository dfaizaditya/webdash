package com.kbbukopin.webdash.enums;

public enum CategoryType {
    PROYEK("Proyek"),
    INSIDEN("Insiden");

    private final String name;

    private CategoryType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

