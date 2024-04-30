/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.ballerina.plugins.idea.sdk;

import com.intellij.openapi.diagnostic.Logger;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

/**
 * Contains util classes related to Ballerina SDK.
 *
 * @since 2.0.0
 */
public class BallerinaSdkUtils {

    private static final Logger LOG = Logger.getInstance(BallerinaSdkUtils.class);
    private static final String BAL_CMD_START = "bal";
    private static final String BAL_VERSION_CMD = "-v";
    private static final String BAL_HOME_CMD = "home";
    private static final String WINDOWS_BAL_EXECUTABLE = "bal.bat";
    private static final String UNIX_BAL_EXECUTABLE = "bal";
    private static final String BAL_EXECUTABLE_FOLDER = "bin";
    private static final String BAL_DIST_FOLDER_NAME_START = "ballerina-";
    private static final String BAL_WINDOWS_ENV_VARIABLE = "BALLERINA_HOME";

    private static String runCommand(String[] cmd) {
        String[] cmdArray;

        if (OSUtils.isWindows()) {
            cmdArray = new String[cmd.length + 2];
            cmdArray[0] = "cmd.exe";
            cmdArray[1] = "/c";
            System.arraycopy(cmd, 0, cmdArray, 2, cmd.length);
        } else {
            cmdArray = cmd;
        }

        try {
            Process process = Runtime.getRuntime().exec(cmdArray);

            Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\\A");
            String output = scanner.hasNext() ? scanner.next() : "";
            LOG.info("Command output: " + output);
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return "";
            }

            return output.trim();

        } catch (Exception e) {
            LOG.error("Error occurred while running command: " + String.join(" ", cmd), e);
            return "";
        }
    }


    public static String getBallerinaVersion() {
        String version;
        try {
            version = runCommand(new String[]{BAL_CMD_START, BAL_VERSION_CMD});
            if (version.contains(")")) {
                version = version.substring(0, version.lastIndexOf(")") + 1);
            }
        } catch (Exception e) {
            LOG.error("Error occurred while getting Ballerina version", e);
            return "";
        }
        LOG.info("Ballerina version: " + version);
        return version.isEmpty() ? "" : version;
    }

    public static String getBallerinaPath() {
        try {
            String version = runCommand(new String[]{BAL_CMD_START, BAL_VERSION_CMD});
            if (Objects.equals(version, "")) {
                LOG.error("Empty Ballerina path");
                return "";
            }
            if (OSUtils.isWindows()) {
                Map<String, String> env = System.getenv();

                for (Map.Entry<String, String> entry : env.entrySet()) {
                    if (Objects.equals(entry.getKey(), BAL_WINDOWS_ENV_VARIABLE)) {
                        String pth = entry.getValue();
                        pth = Paths.get(pth, "distributions", BAL_DIST_FOLDER_NAME_START
                                        + version.split(" ")[1], BAL_EXECUTABLE_FOLDER, WINDOWS_BAL_EXECUTABLE)
                                .normalize().toString();
                        LOG.info("Ballerina path: " + pth);
                        return pth;
                    }
                }
                return "";
            } else {
                return Paths.get(runCommand(new String[]{BAL_CMD_START, BAL_HOME_CMD}),
                        BAL_EXECUTABLE_FOLDER, UNIX_BAL_EXECUTABLE).normalize().toString();
            }
        } catch (Exception e) {
            LOG.error("Error occurred while getting Ballerina path", e);
            return "";
        }
    }

    public static boolean isValidPath(String path) {
        try {
            if (path == null || path.isEmpty()) {
                LOG.info("Empty path");
                return false;
            }
            path = BallerinaSdkUtils.getNormalizedPath(path);
            File file = new File(path);
            if (!file.exists()) {
                LOG.info("File does not exist");
                return false;
            }

            String executableName = file.getName();

            if ((OSUtils.isWindows() && !executableName.equals(WINDOWS_BAL_EXECUTABLE)) ||
                    (!OSUtils.isWindows() && !executableName.equals(UNIX_BAL_EXECUTABLE))) {
                LOG.info("Invalid executable name");
                return false;
            }

            return file.canExecute();
        } catch (Exception e) {
            LOG.error("Error occurred while validating path", e);
            return false;
        }
    }

    public static boolean isValidVersion(String version) {
        return version != null && !version.isEmpty();
    }

    public static boolean isValidSdk(String path, String version) {
        return isValidPath(path) && isValidVersion(version);
    }

    public static String getVersionFromPath(String path) {
        try {
            if (isValidPath(path)) {
                path = BallerinaSdkUtils.getNormalizedPath(path);
                String version = Paths.get(path).getParent().getParent().getFileName().toString();
                version = version.replace('b', 'B').replace('-', ' ');
                String [] parts = version.split("\\.");
                if (parts.length < 2) {
                    return "";
                }
                String update = version.split("\\.")[1];
                version = version + " (Swan Lake Update " + update + ")";
                LOG.info("Version from path: " + version);
                return isValidVersion(version) ? version : "";
            }
            return "";
        } catch (Exception e) {
            LOG.error("Error occurred while getting version from path", e);
            return "";
        }
    }

    public static List<BallerinaSdk> getBallerinaSdks(String ballerinaPath) {
        List<BallerinaSdk> sdkList = new ArrayList<>();
        try {
            if (isValidPath(ballerinaPath)) {
                ballerinaPath = BallerinaSdkUtils.getNormalizedPath(ballerinaPath);
                File sdkDir = new File(ballerinaPath);
                File distRoot = sdkDir.getParentFile().getParentFile().getParentFile();
                File[] files = distRoot.listFiles(
                        (current, name) -> new File(current, name).isDirectory()
                                && name.startsWith(BAL_DIST_FOLDER_NAME_START));
                if (files != null) {
                    for (File file : files) {
                        String version = file.getName()
                                .replace('b', 'B').replace('-', ' ');
                        String update = version.split("\\.")[1];
                        version = version + " (Swan Lake Update " + update + ")";
                        String executableName = OSUtils.isWindows() ? WINDOWS_BAL_EXECUTABLE : UNIX_BAL_EXECUTABLE;
                        Path sdkPath =
                                Paths.get(file.getAbsolutePath(), BAL_EXECUTABLE_FOLDER, executableName).normalize();
                        if (isValidSdk(sdkPath.toString(), version)) {
                            LOG.info("Ballerina SDK: " + sdkPath + " " + version);
                            sdkList.add(new BallerinaSdk(sdkPath.toString(), version));
                        }
                    }
                }
            }
            return sdkList;
        } catch (Exception e) {
            LOG.error("Error occurred while getting Ballerina SDKs", e);
            return sdkList;
        }
    }

    public static String findBalDistFolder(String initialPath) {
        try {
            File current = new File(Paths.get(initialPath).normalize().toString());

            while (current != null) {
                if (current.getName().toLowerCase().contains(BAL_DIST_FOLDER_NAME_START)) {
                    return BallerinaSdkUtils.getNormalizedPath(current.getAbsolutePath());
                }
                LOG.info("Current path: " + current.getAbsolutePath());
                current = current.getParentFile();
            }

            return "";
        } catch (Exception e) {
            LOG.error("Error occurred while finding Ballerina distribution folder", e);
            return "";
        }
    }

    public static String getBalBatFromDist(String distPath) {
        try {
            Path path = Paths.get(distPath).normalize();
            String lastElement = path.getFileName().toString();

            String executableName = OSUtils.isWindows() ? WINDOWS_BAL_EXECUTABLE : UNIX_BAL_EXECUTABLE;

            if (BAL_EXECUTABLE_FOLDER.equals(lastElement)) {
                return path.resolve(executableName).toString();
            } else {
                return path.resolve(Paths.get(BAL_EXECUTABLE_FOLDER, executableName)).toString();
            }
        } catch (Exception e) {
            LOG.error("Error occurred while getting bal.bat from distribution", e);
            return "";
        }
    }

    public static String getNormalizedPath(String path) {
        try {
            if (path.isEmpty()) {
                return "";
            }
            return Paths.get(path).normalize().toString();
        } catch (Exception e) {
            LOG.error("Error occurred while normalizing path", e);
            return "";
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
