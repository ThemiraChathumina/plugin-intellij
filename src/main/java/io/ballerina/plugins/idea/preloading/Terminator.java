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

import com.intellij.openapi.diagnostic.Logger;
import io.ballerina.plugins.idea.BallerinaConstants;

/**
 * Launcher terminator Interface.
 *
 * @since 2.0.0
 */
abstract class Terminator {

    static final Logger LOGGER = Logger.getInstance(Terminator.class);
    static final String LS_CMD_PROCESS_ID = BallerinaConstants.BAL_LS_CMD;
    abstract void terminate();
}
