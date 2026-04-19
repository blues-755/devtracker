package com.devtracker.services;

import com.devtracker.Application;
import com.devtracker.models.Deployment;
import com.devtracker.models.Project;
import com.devtracker.repositories.DeploymentRepository;
import com.devtracker.repositories.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest (classes = Application.class)
@ActiveProfiles("test")
@Transactional
class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private DeploymentService deploymentService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private DeploymentRepository deploymentRepository;

    @BeforeEach
    void setUp() {
        deploymentRepository.deleteAll();
        projectRepository.deleteAll();
    }

    // ── Project tests ────────────────────────────

    @Test
    void shouldSaveAndRetrieveProject() {
        Project project = new Project("MyApp", "A test project", "https://github.com/org/myapp");
        Project saved = projectService.saveProject(project);

        assertNotNull(saved.getId());
        assertEquals("MyApp", saved.getName());
        assertEquals(1, projectService.getAllProjects().size());
    }

    @Test
    void shouldRejectProjectWithEmptyName() {
        Project project = new Project("", "desc", "url");
        assertThrows(IllegalArgumentException.class, () -> projectService.saveProject(project));
    }

    @Test
    void shouldRejectDuplicateProjectName() {
        projectService.saveProject(new Project("MyApp", "First", "url1"));
        assertThrows(IllegalArgumentException.class,
                () -> projectService.saveProject(new Project("MyApp", "Second", "url2")));
    }

    @Test
    void shouldDeleteProject() {
        Project saved = projectService.saveProject(new Project("ToDelete", "desc", "url"));
        projectService.deleteProject(saved.getId());
        assertEquals(0, projectService.getAllProjects().size());
    }

    // ── Deployment tests ─────────────────────────

    @Test
    void shouldCreateDeployment() {
        Project project = projectService.saveProject(new Project("API", "desc", "url"));
        Deployment deployment = deploymentService.createDeployment(
                project, "v1.0.0", Deployment.Environment.DEV, "alice", "Initial release"
        );

        assertNotNull(deployment.getId());
        assertEquals(Deployment.Status.IN_PROGRESS, deployment.getStatus());
        assertEquals("v1.0.0", deployment.getVersion());
    }

    @Test
    void shouldUpdateDeploymentStatus() {
        Project project = projectService.saveProject(new Project("API2", "desc", "url"));
        Deployment deployment = deploymentService.createDeployment(
                project, "v1.0.0", Deployment.Environment.STAGING, "bob", "Notes"
        );

        deploymentService.updateStatus(deployment.getId(), Deployment.Status.SUCCESS);
        Deployment updated = deploymentRepository.findById(deployment.getId()).orElseThrow();
        assertEquals(Deployment.Status.SUCCESS, updated.getStatus());
    }

    @Test
    void shouldRejectDeploymentWithEmptyVersion() {
        Project project = projectService.saveProject(new Project("API3", "desc", "url"));
        assertThrows(IllegalArgumentException.class, () ->
                deploymentService.createDeployment(project, "", Deployment.Environment.PROD, "charlie", "")
        );
    }
}