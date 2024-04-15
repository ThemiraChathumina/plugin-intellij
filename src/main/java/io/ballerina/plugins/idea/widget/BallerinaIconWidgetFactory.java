package io.ballerina.plugins.idea.widget;

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

    public static BallerinaIconWidget getWidget(Project project) {
        return widgetForProject.get(project);
    }

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
        return true;
    }

    @Override
    public @NotNull StatusBarWidget createWidget(@NotNull Project project) {
        return widgetForProject.computeIfAbsent(project, BallerinaIconWidget::new);
    }

    @Override
    public void disposeWidget(@NotNull StatusBarWidget statusBarWidget) {
        if (statusBarWidget instanceof BallerinaIconWidget) {
            widgetForProject.remove(((BallerinaIconWidget) statusBarWidget).getProject());
        }
    }

    @Override
    public boolean canBeEnabledOn(@NotNull StatusBar statusBar) {
        return true;
    }
}
