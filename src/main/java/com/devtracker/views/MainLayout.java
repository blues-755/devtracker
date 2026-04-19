package com.devtracker.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 appName = new H1("Deployment Tracker");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), appName);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames(LumoUtility.Padding.Vertical.NONE, LumoUtility.Padding.Horizontal.MEDIUM);

        addToNavbar(header);
    }

    private void createDrawer() {
        SideNav nav = new SideNav();

        SideNavItem dashboardLink = new SideNavItem("Dashboard", DashboardView.class, VaadinIcon.DASHBOARD.create());
        SideNavItem projectsLink = new SideNavItem("Projects", ProjectsView.class, VaadinIcon.CUBES.create());
        SideNavItem deploymentsLink = new SideNavItem("Deployments", DeploymentsView.class, VaadinIcon.ROCKET.create());

        nav.addItem(dashboardLink, projectsLink, deploymentsLink);
        addToDrawer(nav);
    }
}