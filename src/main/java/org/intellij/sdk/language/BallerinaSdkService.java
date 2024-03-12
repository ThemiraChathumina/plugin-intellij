package org.intellij.sdk.language;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BallerinaSdkService {

    private static BallerinaSdkService instance;
    private final String ballerinaVersion;
    private final String ballerinaPath;

    private BallerinaSdkService() {
        // This code will only run once when the IDE starts and the first access to BallerinaSdkService occurs
        ballerinaVersion = BallerinaSdkDetection.getBallerinaVersion();
        ballerinaPath = BallerinaSdkDetection.getBallerinaPath();
    }

    public static synchronized BallerinaSdkService getInstance() {
        if (instance == null) {
            instance = new BallerinaSdkService();
        }
        return instance;
    }

    public static void instantiate(){
        if (instance == null) {
            instance = new BallerinaSdkService();
        }
    }

    public String getBallerinaVersion() {
        return ballerinaVersion;
    }

    public String getBallerinaPath() {
        return ballerinaPath;
    }

    public boolean isValidPath(String path) {
        if (path == null) {
            return false;
        } else if (!new File(path).exists()) {
            return false;
        } else if (!new File(path).getName().equals("bal.bat")) {
            return false;
        } else return new File(path).canExecute();
    }

    public boolean isValidVersion(String version) {
        return version != null;
    }

    public boolean isValidSdk(String path, String version) {
        return isValidPath(path) && isValidVersion(version);
    }

    public List<MiniSdk> getSdks() {
        List<MiniSdk> sdkList = new ArrayList<>();
        File sdkDir = new File(ballerinaPath);
        File distRoot = sdkDir.getParentFile().getParentFile().getParentFile();
        File[] files = distRoot.listFiles(
                (current, name) -> new File(current, name).isDirectory() && name.startsWith("ballerina-")
        );
        if (files != null) {
            for (File file : files) {
                String version = file.getName();
                System.out.println(file.getName());
                Path sdkPath = Paths.get(file.getAbsolutePath(), "bin", "bal.bat");
                System.out.println(sdkPath.toString());
                System.out.println(version);
                sdkList.add(new MiniSdk(sdkPath.toString(), version));
            }
        }
        return sdkList;
    }

    public static class MiniSdk {
        private final String path;
        private final String version;

        public MiniSdk(String path, String version) {
            this.path = path;
            this.version = version;
        }

        public String getPath() {
            return path;
        }

        public String getVersion() {
            return version;
        }
    }
}
