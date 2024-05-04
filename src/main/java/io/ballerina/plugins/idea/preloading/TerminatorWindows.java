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

package io.ballerina.plugins.idea.preloading;

import static io.ballerina.plugins.idea.BallerinaConstants.BAL_LOG_PREFIX;

/**
 * Launcher Terminator Implementation for Windows. ( Xp professional SP2++).
 *
 * @since 2.0.0
 */
public class TerminatorWindows extends Terminator {

    /**
     * @return file process command.
     */
    private String getFindProcessCommand(String processName) {
        // Escapes forward slashes.
        return "cmd /c wmic.exe Process where \"Commandline like '%" + processName + "%'\" CALL TERMINATE";
    }

    public void terminate() {
        terminate(LS_CMD_PROCESS_ID);
    }

    /**
     * Terminates a given ballerina process.
     */
    private void terminate(String processName) {
        String findProcessCommand = getFindProcessCommand(processName);
        try {
            Process findProcess = Runtime.getRuntime().exec(findProcessCommand);
            findProcess.waitFor();
        } catch (Throwable e) {
            LOGGER.error(BAL_LOG_PREFIX + "Launcher was unable to find the process ID for " + processName + ".");
        }
    }
}
