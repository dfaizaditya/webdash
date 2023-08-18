package com.kbbukopin.webdash.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PinnedProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "project_id", referencedColumnName = "id"),
        @JoinColumn(name = "project_month", referencedColumnName = "month"),
        @JoinColumn(name = "project_unit", referencedColumnName = "unit"),
        @JoinColumn(name = "project_period_id", referencedColumnName = "period_id")
    })
    private Project project;

    // Add any other necessary fields

}

