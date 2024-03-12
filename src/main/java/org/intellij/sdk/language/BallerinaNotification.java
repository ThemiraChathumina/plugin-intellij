package org.intellij.sdk.language;

import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class BallerinaNotification {
    public static void notifyBallerinaNotDetected(Project project) {
        // Use the builder pattern to create a notification.
        Notification notification = NotificationGroupManager.getInstance()
                .getNotificationGroup("Ballerina Plugin Notifications")
                .createNotification("Unable to detect Ballerina in your environment.", NotificationType.ERROR)
                .setTitle("Unable to detect Ballerina in your environment.")
                .setContent("If you just installed Ballerina, you may need to restart the IDE. If not, please install Ballerina.")
                .addAction(NotificationAction.createSimple("Download Ballerina", () -> {
                    BrowserUtil.browse("https://ballerina.io/downloads/");
                }));

        // Show the notification in the given project.
        notification.notify(project);
    }

    public static void customNotification(Project project, String msg) {
        // Use the builder pattern to create a notification.
        Notification notification = NotificationGroupManager.getInstance()
                .getNotificationGroup("Ballerina Plugin Notifications")
                .createNotification("", NotificationType.ERROR)
                .setTitle("")
                .setContent(msg);

        // Show the notification in the given project.
        notification.notify(project);
    }
}
