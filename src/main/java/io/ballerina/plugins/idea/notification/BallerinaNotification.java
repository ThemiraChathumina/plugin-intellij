package io.ballerina.plugins.idea.notification;

import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

public class BallerinaNotification {

    public static void notifyBallerinaNotDetected(Project project) {
        // Use the builder pattern to create a notification.
        Notification notification =
                NotificationGroupManager
                        .getInstance().getNotificationGroup("Ballerina Plugin Notifications")
                        .createNotification("Unable to detect Ballerina in your environment.",
                                NotificationType.ERROR)
                        .setTitle("Unable to detect Ballerina in your environment.")
                        .setContent(
                                "If you just installed Ballerina, you may need " +
                                        "to restart the IDE. If not, please install Ballerina.")
                        .addAction(NotificationAction.createSimple("Download Ballerina", () -> {
                            BrowserUtil.browse("https://ballerina.io/downloads/");
                        }));

        notification.notify(project);
    }

    public static void notifyRestartIde(Project project) {
        Notification notification =
                NotificationGroupManager.getInstance().getNotificationGroup("Ballerina Plugin Notifications")
                        .createNotification("Restart the IDE to apply the changes.", NotificationType.INFORMATION)
                        .setTitle("Restart the IDE to apply the changes.")
                        .setContent("Please restart the IDE to apply the changes.")
                        .addAction(NotificationAction.createSimple("Restart", () -> {
                            // Restart all opened IDEs
                            ApplicationManager.getApplication().restart();
                        }));
        notification.notify(project);
    }

    public static void customNotification(Project project, String msg) {
        Notification notification =
                NotificationGroupManager.getInstance().getNotificationGroup("Ballerina Plugin Notifications")
                        .createNotification("", NotificationType.ERROR).setTitle("").setContent(msg);

        notification.notify(project);
    }
}
