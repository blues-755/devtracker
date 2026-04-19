package com.devtracker.views;

import com.devtracker.models.Project;
import com.devtracker.services.ProjectService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.format.DateTimeFormatter;

@Route(value = "projects", layout = MainLayout.class)
@PageTitle("Projects | Deployment Tracker")
public class ProjectsView extends VerticalLayout {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final ProjectService projectService;
    private final Grid<Project> grid = new Grid<>(Project.class, false);

    public ProjectsView(ProjectService projectService) {
        this.projectService = projectService;

        setSpacing(true);
        setPadding(true);

        HorizontalLayout header = new HorizontalLayout(new H2("Projects"), buildAddButton());
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setWidthFull();

        configureGrid();
        add(header, grid);
        refreshGrid();
    }

    private void configureGrid() {
        grid.addColumn(Project::getName).setHeader("Name").setAutoWidth(true).setSortable(true);
        grid.addColumn(Project::getDescription).setHeader("Description").setFlexGrow(1);
        grid.addColumn(Project::getRepoUrl).setHeader("Repository").setAutoWidth(true);
        grid.addColumn(p -> p.getCreatedAt().format(FORMATTER)).setHeader("Created").setAutoWidth(true);
        grid.addColumn(Project::getDeploymentCount).setHeader("Deployments").setAutoWidth(true);

        grid.addComponentColumn(project -> {
            Button edit = new Button("Edit", e -> openProjectDialog(project));
            Button delete = new Button("Delete", e -> deleteProject(project));
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            return new HorizontalLayout(edit, delete);
        }).setHeader("Actions").setAutoWidth(true);

        grid.setAllRowsVisible(true);
    }

    private Button buildAddButton() {
        Button add = new Button("Add Project", e -> openProjectDialog(null));
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return add;
    }

    private void openProjectDialog(Project existing) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(existing == null ? "New Project" : "Edit Project");

        TextField nameField = new TextField("Project Name");
        TextField descField = new TextField("Description");
        TextField repoField = new TextField("Repository URL");

        if (existing != null) {
            nameField.setValue(existing.getName() != null ? existing.getName() : "");
            descField.setValue(existing.getDescription() != null ? existing.getDescription() : "");
            repoField.setValue(existing.getRepoUrl() != null ? existing.getRepoUrl() : "");
        }

        FormLayout form = new FormLayout(nameField, descField, repoField);
        dialog.add(form);

        Button save = new Button("Save", e -> {
            try {
                Project project = existing != null ? existing : new Project();
                project.setName(nameField.getValue());
                project.setDescription(descField.getValue());
                project.setRepoUrl(repoField.getValue());
                projectService.saveProject(project);
                refreshGrid();
                dialog.close();
                Notification.show("Project saved", 3000, Notification.Position.BOTTOM_START);
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

    private void deleteProject(Project project) {
        projectService.deleteProject(project.getId());
        refreshGrid();
        Notification.show("Project deleted", 3000, Notification.Position.BOTTOM_START);
    }

    private void refreshGrid() {
        grid.setItems(projectService.getAllProjects());
    }
}