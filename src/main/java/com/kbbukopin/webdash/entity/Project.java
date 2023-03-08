package com.kbbukopin.webdash.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
public class Project {

  @Id
  @Column(name = "id")
  private long id;

  @Column(name = "unit")
  private String unit;

  @Column(name = "category")
  private String category;

  @Column(name = "name")
  private String name;

  @Column(name = "user_sponsor")
  private String userSponsor;

  @Column(name = "app_platform")
  private String appPlatform;

  @Column(name = "tech_platform")
  private String techPlatform;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
  @Column(name = "start_date")
  private LocalDate startDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
  @Column(name = "due_date")
  private LocalDate dueDate;
}

