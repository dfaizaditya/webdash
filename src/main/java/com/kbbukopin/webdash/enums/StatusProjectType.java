package com.kbbukopin.webdash.enums;

public enum StatusProjectType {
    DAFTAR_PROYEK("Daftar Proyek/Open Ticket"),
    ANALISA_DESAIN("Analisa Desain/Analisis"),
    DEVELOPMENT("Development/Fixing"),
    SIT("SIT"),
    PRA_UAT("Pra UAT"),
    UAT("UAT"),
    PRE_ROLLOUT("Pre Rollout"),
    ROLLOUT("Rollout/Solved");

    private final String name;

    private StatusProjectType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
