package com.kbbukopin.webdash.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "project")
public class Project {

  @Id
  @Column(name = "id")
  private long id;

  @Column(name = "title")
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "published")
  private boolean published;

  public Project() {

  }

  public Project(String title, String description, boolean published) {
    this.title = title;
    this.description = description;
    this.published = published;
  }

  @Override
  public String toString() {
    return "Project [id=" + id + ", title=" + title + ", desc=" + description + ", published=" + published + "]";
  }

}

