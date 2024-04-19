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

public class BallerinaSdkUtil {

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
            if (exitCode != 0) {
                return "";
            }
            return line;
        } catch (IOException | InterruptedException e) {
            return "";
        }
    }

    public static String getBallerinaVersion() {
        String version = runCommand("bal -v");
        return version == null ? "" : version;
    }

    public static String getBallerinaPath() {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        String version = runCommand("bal -v");
        if (Objects.equals(version, "")) {
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

    public static String getVersionNumber(String version) {
        if (isValidVersion(version)) {
            return version.split("[ \\-]")[1];
        }
        return "";
    }

    public static boolean areEqualVersions(String version1, String version2) {
        if (!isValidVersion(version1) || !isValidVersion(version2)) {
            return false;
        }
        version1 = getVersionNumber(version1);
        version2 = getVersionNumber(version2);
        return Objects.equals(version1, version2);
    }

    public static boolean isValidPath(String path) {
        if (path == null | path.isEmpty()) {
            return false;
        }
        path = Paths.get(path).normalize().toString();
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
        return version != null && !version.isEmpty();
    }

    public static boolean isValidSdk(String path, String version) {
        return isValidPath(path) && isValidVersion(version);
    }

    public static String getVersionFromPath(String path) {
        if (isValidPath(path)) {
            String version = Paths.get(path).getParent().getParent().getFileName().toString();
            version = version.replace('b', 'B').replace('-', ' ');
            String [] parts = version.split("\\.");
            if (parts.length < 2) {
                return "";
            }
            String update = version.split("\\.")[1];
            version = version + " (Swan Lake Update " + update + ")";
            return isValidVersion(version) ? version : "";
        }
        return "";
    }

    public static List<BallerinaSdk> getBallerinaSdks(String ballerinaPath) {
        List<BallerinaSdk> sdkList = new ArrayList<>();
        if (isValidPath(ballerinaPath)) {
            String osName = System.getProperty("os.name").toLowerCase();
            File sdkDir = new File(ballerinaPath);
            File distRoot = sdkDir.getParentFile().getParentFile().getParentFile();
            File[] files = distRoot.listFiles(
                    (current, name) -> new File(current, name).isDirectory() && name.startsWith("ballerina-"));
            if (files != null) {
                for (File file : files) {
                    String version = file.getName().replace('b', 'B').replace('-', ' ');
                    String update = version.split("\\.")[1];
                    version = version + " (Swan Lake Update " + update + ")";
                    String executableName = osName.contains("win") ? "bal.bat" : "bal";
                    Path sdkPath = Paths.get(file.getAbsolutePath(), "bin", executableName);
                    if (isValidSdk(sdkPath.toString(), version)) {
                        sdkList.add(new BallerinaSdk(sdkPath.toString(), version));
                    }
                }
            }
        }
        return sdkList;
    }

    public static String findBalDistFolder(String initialPath) {
        Path currentPath = Paths.get(initialPath);
        while (currentPath != null) {
            if (currentPath.getFileName().toString().toLowerCase().contains("ballerina")) {
                return currentPath.normalize().toString();
            }
            currentPath = currentPath.getParent();
        }
        return "";
    }

    public static String getBalBatFromDist(String distPath) {
        Path path = Paths.get(distPath).normalize();
        String lastElement = path.getFileName().toString();

        String executableName = "bal";
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            executableName += ".bat";
        }

        if ("bin".equals(lastElement)) {
            return path.resolve(executableName).toString();
        } else {
            return path.resolve(Paths.get("bin", executableName)).toString();
        }
    }

    public static class BallerinaSdk {

        private final String path;
        private final String version;

        public BallerinaSdk(String path, String version) {
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
