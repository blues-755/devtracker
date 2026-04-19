package com.devtracker.services;

import com.devtracker.models.Deployment;
import com.devtracker.models.Deployment.Environment;
import com.devtracker.models.Deployment.Status;
import com.devtracker.models.Project;
import com.devtracker.repositories.DeploymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DeploymentService {

    private final DeploymentRepository deploymentRepository;

    public DeploymentService(DeploymentRepository deploymentRepository) {
        this.deploymentRepository = deploymentRepository;
    }

    public List<Deployment> getDeploymentsForProject(Project project) {
        return deploymentRepository.findByProjectOrderByDeployedAtDesc(project);
    }

    public List<Deployment> getDeploymentsByEnvironment(Environment environment) {
        return deploymentRepository.findByEnvironmentOrderByDeployedAtDesc(environment);
    }

    public List<Deployment> getRecentDeployments() {
        return deploymentRepository.findTop10ByOrderByDeployedAtDesc();
    }

    public List<Deployment> getFailedDeployments() {
        return deploymentRepository.findByStatusOrderByDeployedAtDesc(Status.FAILED);
    }

    public Deployment createDeployment(Project project, String version,
                                       Environment environment, String deployedBy, String notes) {
        if (version == null || version.isBlank()) {
            throw new IllegalArgumentException("Version cannot be empty");
        }
        if (deployedBy == null || deployedBy.isBlank()) {
            throw new IllegalArgumentException("Deployed by cannot be empty");
        }
        Deployment deployment = new Deployment(project, version, environment, deployedBy, notes);
        return deploymentRepository.save(deployment);
    }

    public Optional<Deployment> updateStatus(Long deploymentId, Status newStatus) {
        return deploymentRepository.findById(deploymentId).map(d -> {
            d.setStatus(newStatus);
            return deploymentRepository.save(d);
        });
    }

    public long getTotalDeploymentCount() {
        return deploymentRepository.count();
    }

    public long getFailedDeploymentCount() {
        return deploymentRepository.findByStatusOrderByDeployedAtDesc(Status.FAILED).size();
    }
}