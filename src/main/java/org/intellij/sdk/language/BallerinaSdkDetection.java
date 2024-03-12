package org.intellij.sdk.language;

import com.intellij.util.SlowOperations;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

public class BallerinaSdkDetection {

    public static void main(String[] args) {
        System.out.println(getBallerinaVersion());
        System.out.println(getBallerinaPath());
    }


    // Run a command in the terminal and return the first line of the output
    public static String runCommand(String cmd) {
        SlowOperations.assertSlowOperationsAreAllowed();
        // Use ProcessBuilder for better control and flexibility
        ProcessBuilder processBuilder = new ProcessBuilder();

        // Check the operating system to set the command correctly
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        if (isWindows) {
            // In Windows, wrap the command in cmd.exe
            processBuilder.command("cmd.exe", "/c", cmd);
        } else {
            // In Unix-based systems (Linux, macOS), use sh
            processBuilder.command("sh", "-c", cmd);
        }

        try {
            // Start the process
            Process process = processBuilder.start();

            // Read the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();

            // Wait for the command to finish
            int exitCode = process.waitFor();
            return line;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
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

            // Iterate through the map and print each variable and its value
            for (Map.Entry<String, String> entry : env.entrySet()) {
                if (Objects.equals(entry.getKey(), "BALLERINA_HOME")) {
                    String pth = entry.getValue();
                    pth = Paths.get(pth,"distributions", "ballerina-" + version.split(" ")[1], "bin", "bal.bat" ).toString();
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

        // Traverse up the file system from the current file/directory
//        File current = file.getParentFile();

        while (current != null) {
            File ballerinaFile = new File(current, "Ballerina.toml");
            // Check if Ballerina.toml exists in the current directory
            if (ballerinaFile.exists()) {
                return current.getAbsolutePath(); // Return the path if found
            }
            // Move up one directory
            current = current.getParentFile();
        }

        // Return an empty string if Ballerina.toml is not found
        return "";
    }

    public static String findBallerinaModule(String startingPath) {

        File current = new File(startingPath);

        // Traverse up the file system from the current file/directory

        while (current != null) {
            if (current.getParentFile() != null && current.getParentFile().getName().equalsIgnoreCase("modules")) {
                if (new File(current.getParentFile().getParentFile(), "Ballerina.toml").exists()) {
                    return current.getAbsolutePath(); // Return the path if found
                }
            }
            current = current.getParentFile();
        }

        return "";
    }

}
