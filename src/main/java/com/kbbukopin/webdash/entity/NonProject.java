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

    @Column(name = "no_tiket_or_rfc")
    private long noTiketOrRfc;

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

    @Column(name = "category_project")
    private String categoryProject;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "non_project_user_sponsor",
            joinColumns = {
                    @JoinColumn(name = "non_project_id", referencedColumnName = "id")
            },
            inverseJoinColumns = @JoinColumn(name = "user_sponsor_id"))
    private List<UserSponsor> userSponsor;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "non_project_app_platform",
            joinColumns = {
                    @JoinColumn(name = "non_project_id", referencedColumnName = "id")
            },
            inverseJoinColumns = @JoinColumn(name = "app_platform_id"))
    private List<AppPlatform> appPlatform;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "non_project_tech_platform",
            joinColumns = {
                    @JoinColumn(name = "project_id", referencedColumnName = "id")
            },
            inverseJoinColumns = @JoinColumn(name = "tech_platform_id"))
    private List<TechPlatform> techPlatform;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "non_project_pic",
            joinColumns = {
                    @JoinColumn(name = "non_project_id", referencedColumnName = "id")
            },
            inverseJoinColumns = @JoinColumn(name = "pic_id"))
    private List<Pic> pic;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    @Column(name = "start_date")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    @Column(name = "due_date")
    private LocalDate dueDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    @Column(name = "finished_date")
    private LocalDate finishedDate;

    @Column(name = "type")
    private String type;

    @Column(name = "progress")
    private BigDecimal progress;

    @Column(name = "status")
    private String status;

    @Column(name = "info1")
    private String info1;

    @Column(name = "change_type")
    private String changeType;

    @Column(name = "rfc")
    private String rfc;

    @Column(name = "documentation")
    private String documentation;

    @Column(name = "info2", columnDefinition="TEXT")
    private String info2;

}

