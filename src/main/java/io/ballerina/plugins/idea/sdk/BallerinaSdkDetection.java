package io.ballerina.plugins.idea.sdk;

import com.intellij.util.SlowOperations;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BallerinaSdkDetection {

    private static List<MiniSdk> availableSdks;

    public static String runCommand(String cmd) {
        SlowOperations.assertSlowOperationsAreAllowed();
        ProcessBuilder processBuilder = new ProcessBuilder();

        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        if (isWindows) {
            processBuilder.command("cmd.exe", "/c", cmd);
        } else {
            processBuilder.command("sh", "-c", cmd);
        }

        try {
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();

            int exitCode = process.waitFor();
            return line;
        } catch (IOException | InterruptedException e) {
            return "error";
        }
    }

    public static String getBallerinaVersion() {
        return runCommand("bal -v");
    }

    public static String getBallerinaPath() {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        String version = runCommand("bal -v");
        if (version == null) {
            return "";
        }
        if (isWindows) {
            Map<String, String> env = System.getenv();

            for (Map.Entry<String, String> entry : env.entrySet()) {
                if (Objects.equals(entry.getKey(), "BALLERINA_HOME")) {
                    String pth = entry.getValue();
                    pth = Paths.get(pth, "distributions", "ballerina-" + version.split(" ")[1], "bin", "bal.bat")
                            .toString();
                    return pth;
                }
            }
            return "";
        } else {
            return Paths.get(runCommand("bal home"), "bin", "bal").toString();
        }
    }

    public static String findBallerinaPackage(String startingPath) {

        File current = new File(startingPath);

        while (current != null) {
            File ballerinaFile = new File(current, "Ballerina.toml");
            if (ballerinaFile.exists()) {
                return current.getAbsolutePath(); // Return the path if found
            }
            current = current.getParentFile();
        }

        return "";
    }

    public static String findBallerinaModule(String startingPath) {

        File current = new File(startingPath);

        while (current != null) {
            if (current.getParentFile() != null && current.getParentFile().getName().equalsIgnoreCase("modules")) {
                if (new File(current.getParentFile().getParentFile(), "Ballerina.toml").exists()) {
                    return current.getAbsolutePath();
                }
            }
            current = current.getParentFile();
        }

        return "";
    }

    public static boolean isValidPath(String path) {
        if (path == null) {
            return false;
        }

        File file = new File(path);
        if (!file.exists()) {
            return false;
        }

        String osName = System.getProperty("os.name").toLowerCase();
        String executableName = file.getName();

        if ((osName.contains("win") && !executableName.equals("bal.bat")) ||
                (!osName.contains("win") && !executableName.equals("bal"))) {
            return false;
        }

        return file.canExecute();
    }

    public static boolean isValidVersion(String version) {
        return version != null;
    }

    public static boolean isValidSdk(String path, String version) {
        return isValidPath(path) && isValidVersion(version);
    }

    public static List<MiniSdk> getSdks() {
        if (availableSdks != null && !availableSdks.isEmpty()) {
            return availableSdks;
        }
        String osName = System.getProperty("os.name").toLowerCase();
        List<MiniSdk> sdkList = new ArrayList<>();
        File sdkDir = new File(getBallerinaPath());
        File distRoot = sdkDir.getParentFile().getParentFile().getParentFile();
        File[] files = distRoot.listFiles(
                (current, name) -> new File(current, name).isDirectory() && name.startsWith("ballerina-"));
        if (files != null) {
            for (File file : files) {
                String version = file.getName();
                String executableName = osName.contains("win") ? "bal.bat" : "bal";
                Path sdkPath = Paths.get(file.getAbsolutePath(), "bin", executableName);
                if (isValidSdk(sdkPath.toString(), version)) {
                    sdkList.add(new MiniSdk(sdkPath.toString(), version));
                }
            }
        }
        if (availableSdks == null || availableSdks.isEmpty()) {
            availableSdks = sdkList;
        }
        return sdkList;
    }

    public static String getSdkPath(String version) {
        if (availableSdks == null || availableSdks.isEmpty()) {
            return "";
        }
        for (MiniSdk miniSdk: availableSdks) {
            if (Objects.equals(miniSdk.version, version)) {
                return miniSdk.path;
            }
        }
        return "";
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

        @Override
        public String toString() {
            return version + '-' + path;
        }
    }
}
