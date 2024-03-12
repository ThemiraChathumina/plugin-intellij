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

public class BallerinaDetectionWidgetFactory implements StatusBarWidgetFactory{
    private static final Map<Project, BallerinaDetectionWidget> widgetForProject = new HashMap<>();

    @Override
    public @NonNls @NotNull String getId() {
        return "BallerinaDetectionWidget";
    }

    @Override
    public @Nls @NotNull String getDisplayName() {
        return "Ballerina Detection Widget";
    }

    @Override
    public boolean isAvailable(@NotNull Project project) {
        return true;
    }

    @Override
    public @NotNull StatusBarWidget createWidget(@NotNull Project project) {
        return widgetForProject.computeIfAbsent(project, (k) -> new BallerinaDetectionWidget(project));
    }

    @Override
    public void disposeWidget(@NotNull StatusBarWidget statusBarWidget) {
        if (statusBarWidget instanceof BallerinaDetectionWidget) {
            widgetForProject.remove(((BallerinaDetectionWidget) statusBarWidget).getProject());
        }
    }

    @Override
    public boolean canBeEnabledOn(@NotNull StatusBar statusBar) {
        return true;
    }

    public static BallerinaDetectionWidget getWidget(Project project) {
        return widgetForProject.get(project);
    }


}
