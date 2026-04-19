package com.devtracker.repositories;

import com.devtracker.models.Deployment;
import com.devtracker.models.Deployment.Environment;
import com.devtracker.models.Deployment.Status;
import com.devtracker.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeploymentRepository extends JpaRepository<Deployment, Long> {
    List<Deployment> findByProjectOrderByDeployedAtDesc(Project project);
    List<Deployment> findByEnvironmentOrderByDeployedAtDesc(Environment environment);
    List<Deployment> findByStatusOrderByDeployedAtDesc(Status status);
    List<Deployment> findTop10ByOrderByDeployedAtDesc();
}