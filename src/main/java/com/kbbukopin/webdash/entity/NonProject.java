package com.kbbukopin.webdash.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "non_project")
public class NonProject extends DateAudit{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "month")
    private String month;

    @ManyToOne
    @JoinColumn(name = "period_id")
    private Period period;

    @Column(name = "unit")
    private String unit;

    @Column(name = "category")
    private String category;

    @Column(name = "name")
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "non_project_pic",
            joinColumns = {
                    @JoinColumn(name = "non_project_id", referencedColumnName = "id")
            },
            inverseJoinColumns = @JoinColumn(name = "pic_id"))
    private List<Pic> pic;

}

