package io.ballerina.plugins.idea.sdk;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.Pair;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BallerinaSdkService {

    private static BallerinaSdkService instance;

    private final Map<Project, Pair<String,String>> projectSdk = new HashMap<>();

    private BallerinaSdkService() {}

    public static synchronized BallerinaSdkService getInstance() {
        if (instance == null) {
            instance = new BallerinaSdkService();
        }
        return instance;
    }

    private void addNewSdk(Project project) {
        BallerinaSettingsService ballerinaSettingsService = BallerinaSettingsService.getInstance(project);
        String balVersion = ballerinaSettingsService.getBallerinaVersion();
        String balPath = ballerinaSettingsService.getBallerinaRuntimePath();
        if (balVersion == null) {
            balVersion = BallerinaSdkDetection.getBallerinaVersion();
            ballerinaSettingsService.setBallerinaVersion(balVersion);
        }
        if (balPath == null) {
            balPath = BallerinaSdkDetection.getBallerinaPath();
            ballerinaSettingsService.setBallerinaRuntimePath(balPath);
        }
        projectSdk.putIfAbsent(project,new Pair<>(balVersion,balPath));
    }


    public String getBallerinaVersion(Project project) {
        if (projectSdk.containsKey(project)) {
            return projectSdk.get(project).first;
        }
        addNewSdk(project);
        return projectSdk.get(project).first;
    }

    public String getBallerinaPath(Project project) {
        if (projectSdk.containsKey(project)) {
            return projectSdk.get(project).second;
        }
        addNewSdk(project);
        return projectSdk.get(project).second;
    }
}
