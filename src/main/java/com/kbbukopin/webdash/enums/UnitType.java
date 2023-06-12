package com.kbbukopin.webdash.enums;

public enum UnitType {
    CBS("CBS"),
    KREDIT("Kredit"),
    PACKAGE("Package");

    private final String name;

    private UnitType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

