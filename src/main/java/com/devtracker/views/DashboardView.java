package com.devtracker.views;

import com.devtracker.models.Deployment;
import com.devtracker.services.DeploymentService;
import com.devtracker.services.ProjectService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "dashboard", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PageTitle("Dashboard | Deployment Tracker")
public class DashboardView extends VerticalLayout {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    public DashboardView(ProjectService projectService, DeploymentService deploymentService) {
        addClassName("dashboard-view");
        setSpacing(true);
        setPadding(true);

        add(new H2("Dashboard"));
        add(buildStatCards(projectService, deploymentService));
        add(buildRecentDeployments(deploymentService));
    }

    private HorizontalLayout buildStatCards(ProjectService projectService, DeploymentService deploymentService) {
        HorizontalLayout cards = new HorizontalLayout(
                statCard("Total Projects", String.valueOf(projectService.getTotalProjectCount()), "projects"),
                statCard("Total Deployments", String.valueOf(deploymentService.getTotalDeploymentCount()), "all time"),
                statCard("Failed Deployments", String.valueOf(deploymentService.getFailedDeploymentCount()), "need attention")
        );
        cards.setWidthFull();
        return cards;
    }

    private Div statCard(String title, String value, String subtitle) {
        Div card = new Div();
        card.getStyle()
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "8px")
                .set("padding", "20px")
                .set("flex", "1")
                .set("background", "var(--lumo-base-color)");

        Paragraph titleEl = new Paragraph(title);
        titleEl.getStyle().set("color", "var(--lumo-secondary-text-color)").set("margin", "0");

        H3 valueEl = new H3(value);
        valueEl.getStyle().set("margin", "8px 0 4px 0").set("font-size", "2em");

        Paragraph subtitleEl = new Paragraph(subtitle);
        subtitleEl.getStyle().set("color", "var(--lumo-tertiary-text-color)").set("margin", "0").set("font-size", "0.85em");

        card.add(titleEl, valueEl, subtitleEl);
        return card;
    }


    private VerticalLayout buildRecentDeployments(DeploymentService deploymentService) {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.add(new H3("Recent Deployments"));

        List<Deployment> recent = deploymentService.getRecentDeployments();

        Grid<Deployment> grid = new Grid<>(Deployment.class, false);
        grid.addColumn(d -> d.getProject().getName()).setHeader("Project").setAutoWidth(true);
        grid.addColumn(Deployment::getVersion).setHeader("Version").setAutoWidth(true);
        grid.addColumn(Deployment::getEnvironment).setHeader("Environment").setAutoWidth(true);
        grid.addColumn(Deployment::getStatus).setHeader("Status").setAutoWidth(true);
        grid.addColumn(Deployment::getDeployedBy).setHeader("Deployed By").setAutoWidth(true);
        grid.addColumn(d -> d.getDeployedAt().format(FORMATTER)).setHeader("When").setAutoWidth(true);

        grid.setItems(recent);
        grid.setAllRowsVisible(true);

        section.add(grid);
        return section;
    }

}