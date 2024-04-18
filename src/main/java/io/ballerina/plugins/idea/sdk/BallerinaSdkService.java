package io.ballerina.plugins.idea.sdk;

import java.util.List;

public class BallerinaSdkService {

    private static BallerinaSdkService instance;
    private final String ballerinaVersion;
    private final String ballerinaPath;

    private final List<BallerinaSdkUtil.BallerinaSdk> sdkList;

    private BallerinaSdkService() {
        // This code will only run once when the IDE starts and the first access to BallerinaSdkService occurs
        ballerinaVersion = BallerinaSdkUtil.getBallerinaVersion();
        ballerinaPath = BallerinaSdkUtil.getBallerinaPath();
        sdkList = BallerinaSdkUtil.getBallerinaSdks(ballerinaPath);
    }

    public static synchronized BallerinaSdkService getInstance() {
        if (instance == null) {
            instance = new BallerinaSdkService();
        }
        return instance;
    }

    public String getBallerinaVersion() {
        BallerinaSdkSettings settings = BallerinaSdkSettings.getInstance();
        if (settings != null && settings.isUseCustomSdk()) {
            return settings.getBallerinaSdkVersion();
        }
        return ballerinaVersion;
    }

    public String getBallerinaPath() {
        BallerinaSdkSettings settings = BallerinaSdkSettings.getInstance();
        if (settings != null  && settings.isUseCustomSdk()) {
            return settings.getBallerinaSdkPath();
        }
        return ballerinaPath;
    }

    public String getSystemBalPath() {
        return ballerinaPath;
    }

    public String getSystemBalVersion() {
        return ballerinaVersion;
    }

    public void setBallerinaSdk(String path, String version) {
        BallerinaSdkSettings settings = BallerinaSdkSettings.getInstance();
        if (settings == null) {
            return;
        }
        settings.setBallerinaSdkPath(path);
        settings.setBallerinaSdkVersion(version);
    }

    public List<BallerinaSdkUtil.BallerinaSdk> getSdkList() {
        return sdkList;
    }
}
