package io.ballerina.plugins.idea.project;

import java.io.File;

public class BallerinaProjectUtil {

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
            if (current.getParentFile() != null
                    && current.getParentFile().getName().equalsIgnoreCase("modules")) {
                if (new File(current.getParentFile().getParentFile(), "Ballerina.toml").exists()) {
                    return current.getAbsolutePath();
                }
            }
            current = current.getParentFile();
        }

        return "";
    }

}
