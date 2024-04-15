package io.ballerina.plugins.idea.sdk;

import com.intellij.util.SlowOperations;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

public class BallerinaSdkDetection {

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
}
