package com.devtracker.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String repoUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Deployment> deployments = new ArrayList<>();

    //Constructors
    public Project(){}

    public Project(String name, String description, String repoUrl){
        this.name = name;
        this.description = description;
        this.repoUrl = repoUrl;
    }

    //Getters and Setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getRepoUrl() {
        return repoUrl;
    }
    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public List<Deployment> getDeployments() {
        return deployments;
    }
    public void setDeployments(List<Deployment> deployments) {
        this.deployments = deployments;
    }

    public long getDeploymentCount() { return deployments.size(); }

    public Deployment getLatestDeployment() {
        return deployments.stream()
                .max((a, b) -> a.getDeployedAt().compareTo(b.getDeployedAt()))
                .orElse(null);
    }
}
