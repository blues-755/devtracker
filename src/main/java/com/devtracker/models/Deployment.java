package com.devtracker.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deployments")
public class Deployment {

    public enum Environment {DEV, STAGING, PROD }

    public enum Status {IN_PROGRESS, SUCCESS, FAILED, ROLLED_BACK}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Environment environment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.IN_PROGRESS;

    @Column(nullable = false)
    private String deployedBy;

    @Column(nullable = false)
    private LocalDateTime deployedAt = LocalDateTime.now();

    @Column(length = 1000)
    private String notes;

    //Constructors

    public Deployment(){}

    public Deployment(Project project, String version, Environment environment, String deployedBy, String notes) {
        this.project = project;
        this.version = version;
        this.environment = environment;
        this.deployedBy = deployedBy;
        this.notes = notes;
    }

    //Getters and Setters)

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }
    public void setProject(Project project) {
        this.project = project;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDeployedBy() {
        return deployedBy;
    }
    public LocalDateTime getDeployedAt() {
        return deployedAt;
    }

    public void setDeployedBy(String deployedBy) {
        this.deployedBy = deployedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}