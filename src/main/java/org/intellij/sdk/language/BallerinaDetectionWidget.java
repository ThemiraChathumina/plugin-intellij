package org.intellij.sdk.language;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.MouseEvent;

public class BallerinaDetectionWidget implements StatusBarWidget, StatusBarWidget.TextPresentation {

    private static final String ID = "BallerinaDetectionWidget";
    private final Project project;
    private String message = "";

    public BallerinaDetectionWidget(Project project) {
        this.project = project;
    }




    @NotNull
    @Override
    public String ID() {
        return ID;
    }

    public void setMessage(String message) {
        this.message = message;
        // You should also request a UI update to ensure the new message is displayed
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
    }

    @Override
    public void dispose() {
        // Add disposal logic if necessary
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return message;
    }

//    @NotNull
//    @Override
//    public String getMaxPossibleText() {
//        return message;
//    }

    @Override
    public float getAlignment() {
        return Component.CENTER_ALIGNMENT;
    }

    @Nullable
    @Override
    public String getTooltipText() {
        return "Ballerina is currently being detected";
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
