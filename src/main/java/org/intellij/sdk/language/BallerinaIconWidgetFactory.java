package org.intellij.sdk.language;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BallerinaIconWidgetFactory implements StatusBarWidgetFactory {
    private static final Map<Project, BallerinaIconWidget> widgetForProject = new HashMap<>();

    @Override
    public @NonNls @NotNull String getId() {
        return "BallerinaIconWidget";
    }

    @Override
    public @Nls @NotNull String getDisplayName() {
        return "Icon Widget";
    }

    @Override
    public boolean isAvailable(@NotNull Project project) {
        // You can add logic here to determine when the widget should be available
        return true;
    }

    @Override
    public @NotNull StatusBarWidget createWidget(@NotNull Project project) {
        // Use computeIfAbsent to create a new BallerinaIconWidget only if one does not already exist for the project
        return widgetForProject.computeIfAbsent(project, BallerinaIconWidget::new);
    }

    @Override
    public void disposeWidget(@NotNull StatusBarWidget statusBarWidget) {
        // Properly dispose of the BallerinaIconWidget and remove it from the map when no longer needed
        if (statusBarWidget instanceof BallerinaIconWidget) {
            widgetForProject.remove(((BallerinaIconWidget) statusBarWidget).getProject());
        }
    }

    @Override
    public boolean canBeEnabledOn(@NotNull StatusBar statusBar) {
        // Add logic here if there are conditions where the widget should not be enabled
        return true;
    }

    public static BallerinaIconWidget getWidget(Project project) {
        // Provide a static method to retrieve an BallerinaIconWidget instance for a given project
        return widgetForProject.get(project);
    }
}
