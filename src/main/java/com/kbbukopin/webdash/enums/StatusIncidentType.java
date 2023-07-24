package com.kbbukopin.webdash.enums;

public enum StatusIncidentType {
    DAFTAR_PROYEK("Daftar Proyek/Open Ticket"),
    ANALISA_DESAIN("Analisa Desain/Analisis"),
    DEVELOPMENT("Development/Fixing"),
    SIT("SIT"),
    PRE_ROLLOUT("Pre Rollout"),
    ROLLOUT("Rollout/Solved");

    private final String name;

    private StatusIncidentType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
