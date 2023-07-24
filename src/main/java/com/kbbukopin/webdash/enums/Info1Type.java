package com.kbbukopin.webdash.enums;

public enum Info1Type {

    DTIUNDA("Ditunda"),
    ON_GOING_ON_SCHEDULE("On Going(On Schedule)"),
    ON_GOING_OVERDUE("On Going(Overdue)"),
    ON_GOING_NA("On Going(N/A)"),
    FINISHED_AHEAD("Finished(Ahead)"),
    FINISHED_ON_TIME("Finished(On Time)"),
    FINISHED_OVERDUE("Finished(Overdue)"),
    FINISHED_NA("Finished(N/A)"),
    FINISHED_AHEAD_DTIUNDA("Finished(Ahead) - Ditunda"),
    FINISHED_ON_TIME_DITUNDA("Finished(On Time) - Ditunda"),
    FINISHED_OVERDUE_DTIUNDA("Finished(Overdue) - Ditunda"),
    CANCEL("Cancel");

    private final String name;

    private Info1Type(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
