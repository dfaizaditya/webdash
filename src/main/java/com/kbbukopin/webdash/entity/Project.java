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
@Table(name = "project")
@IdClass(ProjectId.class)
public class Project extends DateAudit{

  @Id
  @Column(name = "id")
  private long id;

  @Id
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

//  @Column(name = "user_sponsor")
//  private String userSponsor;
  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "project_user_sponsor",
          joinColumns = {
                  @JoinColumn(name = "project_id", referencedColumnName = "id"),
                  @JoinColumn(name = "project_month", referencedColumnName = "month")
          },
          inverseJoinColumns = @JoinColumn(name = "user_sponsor_id"))
  private List<UserSponsor> userSponsor;

//  @Column(name = "app_platform")
//  private String appPlatform;
  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "project_app_platform",
          joinColumns = {
                  @JoinColumn(name = "project_id", referencedColumnName = "id"),
                  @JoinColumn(name = "project_month", referencedColumnName = "month")
          },
          inverseJoinColumns = @JoinColumn(name = "app_platform_id"))
  private List<AppPlatform> appPlatform;

//  @Column(name = "tech_platform")
//  private String techPlatform;
  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "project_tech_platform",
          joinColumns = {
                  @JoinColumn(name = "project_id", referencedColumnName = "id"),
                  @JoinColumn(name = "project_month", referencedColumnName = "month")
          },
          inverseJoinColumns = @JoinColumn(name = "tech_platform_id"))
  private List<TechPlatform> techPlatform;

//  @Column(name = "pic")
//  private String pic;
  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "project_pic",
          joinColumns = {
                  @JoinColumn(name = "project_id", referencedColumnName = "id"),
                  @JoinColumn(name = "project_month", referencedColumnName = "month")
          },
          inverseJoinColumns = @JoinColumn(name = "pic_id"))
  private List<Pic> pic;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
  @Column(name = "start_date")
  private LocalDate startDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
  @Column(name = "due_date")
  private LocalDate dueDate;

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

