package com.kbbukopin.webdash.enums;

public enum DocumentationType {

    LENGKAP("Lengkap"),
    TIDAK_LENGKAP("Tidak Lengkap");

    private final String name;

    private DocumentationType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
