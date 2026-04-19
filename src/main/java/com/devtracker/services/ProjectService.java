package com.devtracker.services;

import com.devtracker.models.Project;
import com.devtracker.repositories.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public List<Project> searchProjects(String name) {
        return projectRepository.findByNameContainingIgnoreCase(name);
    }

    public Project saveProject(Project project) {
        if (project.getName() == null || project.getName().isBlank()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }
        if (project.getId() == null && projectRepository.existsByName(project.getName())) {
            throw new IllegalArgumentException("A project with this name already exists");
        }
        return projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    public long getTotalProjectCount() {
        return projectRepository.count();
    }
}