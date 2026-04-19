package com.devtracker.views;

import com.devtracker.models.Deployment;
import com.devtracker.models.Deployment.Environment;
import com.devtracker.models.Deployment.Status;
import com.devtracker.models.Project;
import com.devtracker.services.DeploymentService;
import com.devtracker.services.ProjectService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "deployments", layout = MainLayout.class)
@PageTitle("Deployments | Deployment Tracker")
public class DeploymentsView extends VerticalLayout {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final DeploymentService deploymentService;
    private final ProjectService projectService;
    private final Grid<Deployment> grid = new Grid<>(Deployment.class, false);

    public DeploymentsView(DeploymentService deploymentService, ProjectService projectService) {
        this.deploymentService = deploymentService;
        this.projectService = projectService;

        setSpacing(true);
        setPadding(true);

        HorizontalLayout header = new HorizontalLayout(new H2("Deployments"), buildNewDeploymentButton());
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setWidthFull();

        configureGrid();
        add(header, grid);
        refreshGrid();
    }

    private void configureGrid() {
        grid.addColumn(d -> d.getProject().getName()).setHeader("Project").setAutoWidth(true).setSortable(true);
        grid.addColumn(Deployment::getVersion).setHeader("Version").setAutoWidth(true);
        grid.addColumn(Deployment::getEnvironment).setHeader("Environment").setAutoWidth(true);

        // Coloured status badge
        grid.addComponentColumn(d -> {
            Span badge = new Span(d.getStatus().name());
            badge.getStyle().set("border-radius", "4px").set("padding", "2px 8px").set("font-size", "0.85em");
            switch (d.getStatus()) {
                case SUCCESS -> badge.getStyle().set("background", "#d4edda").set("color", "#155724");
                case FAILED -> badge.getStyle().set("background", "#f8d7da").set("color", "#721c24");
                case IN_PROGRESS -> badge.getStyle().set("background", "#fff3cd").set("color", "#856404");
                case ROLLED_BACK -> badge.getStyle().set("background", "#e2e3e5").set("color", "#383d41");
            }
            return badge;
        }).setHeader("Status").setAutoWidth(true);

        grid.addColumn(Deployment::getDeployedBy).setHeader("Deployed By").setAutoWidth(true);
        grid.addColumn(d -> d.getDeployedAt().format(FORMATTER)).setHeader("When").setAutoWidth(true);
        grid.addColumn(Deployment::getNotes).setHeader("Notes").setFlexGrow(1);

        grid.addComponentColumn(deployment -> {
            ComboBox<Status> statusBox = new ComboBox<>();
            statusBox.setItems(Status.values());
            statusBox.setValue(deployment.getStatus());
            statusBox.setWidth("150px");
            statusBox.addValueChangeListener(e -> {
                deploymentService.updateStatus(deployment.getId(), e.getValue());
                refreshGrid();
            });
            return statusBox;
        }).setHeader("Update Status").setAutoWidth(true);

        grid.setAllRowsVisible(true);
    }

    private Button buildNewDeploymentButton() {
        Button btn = new Button("New Deployment", e -> openDeploymentDialog());
        btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return btn;
    }

    private void openDeploymentDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("New Deployment");

        List<Project> projects = projectService.getAllProjects();
        if (projects.isEmpty()) {
            Notification n = Notification.show("Create a project first", 3000, Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        ComboBox<Project> projectBox = new ComboBox<>("Project");
        projectBox.setItems(projects);
        projectBox.setItemLabelGenerator(Project::getName);
        projectBox.setRequired(true);

        TextField versionField = new TextField("Version");
        versionField.setPlaceholder("e.g. v1.2.3");

        ComboBox<Environment> envBox = new ComboBox<>("Environment");
        envBox.setItems(Environment.values());
        envBox.setRequired(true);

        TextField deployedByField = new TextField("Deployed By");

        TextArea notesField = new TextArea("Release Notes");
        notesField.setMaxLength(1000);

        FormLayout form = new FormLayout(projectBox, versionField, envBox, deployedByField, notesField);
        dialog.add(form);

        Button save = new Button("Deploy", e -> {
            try {
                deploymentService.createDeployment(
                        projectBox.getValue(),
                        versionField.getValue(),
                        envBox.getValue(),
                        deployedByField.getValue(),
                        notesField.getValue()
                );
                refreshGrid();
                dialog.close();
                Notification.show("Deployment logged", 3000, Notification.Position.BOTTOM_START);
            } catch (IllegalArgumentException ex) {
                Notification n = Notification.show(ex.getMessage(), 4000, Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancel = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancel, save);
        dialog.open();
    }

    private void refreshGrid() {
        grid.setItems(deploymentService.getRecentDeployments());
    }
}