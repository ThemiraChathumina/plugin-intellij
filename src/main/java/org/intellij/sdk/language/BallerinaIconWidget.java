package org.intellij.sdk.language;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class BallerinaIconWidget implements StatusBarWidget, StatusBarWidget.IconPresentation {

    private static final String ID = "BallerinaIconWidget";
    private final Project project;
    private Icon icon = null; // Icon is null initially
    private String tooltipText = "Default tooltip text"; // Default tooltip text

    public BallerinaIconWidget(Project project) {
        this.project = project;
    }

    @NotNull
    @Override
    public String ID() {
        return ID;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
        // Request a UI update to ensure the new icon is displayed
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        if (statusBar != null) {
            statusBar.updateWidget(ID());
        }
    }

    public void setTooltipText(String tooltipText) {
        this.tooltipText = tooltipText;
        // Request a UI update to ensure the new tooltip text is displayed
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        if (statusBar != null) {
            statusBar.updateWidget(ID());
        }
    }

    @Nullable
    @Override
    public WidgetPresentation getPresentation() {
        return this;
    }

    @Override
    public void install(@NotNull StatusBar statusBar) {
        // Add installation logic if necessary
    }

    @Override
    public void dispose() {
        // Add disposal logic if necessary
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return icon;
    }

    @Nullable
    @Override
    public String getTooltipText() {
        return tooltipText;
    }

    @Override
    public @Nullable Consumer<MouseEvent> getClickConsumer() {
        // Define what happens when the widget is clicked, if needed
        return null;
    }

    public Project getProject() {
        return project;
    }
}
