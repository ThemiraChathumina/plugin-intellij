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

package io.ballerina.plugins.idea.runconfig.application;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.NotNullLazyValue;
import io.ballerina.plugins.idea.BallerinaIcons;

import javax.swing.Icon;

/**
 * Represents Ballerina run configuration type.
 *
 * @since 2.0.0
 */
final class BallerinaApplicationRunConfigType extends ConfigurationTypeBase {

    static final String ID = "BallerinaRunConfiguration";
    static final String EXEC_TYPE = "Run";

    BallerinaApplicationRunConfigType() {
        super(ID, "Ballerina Run", "Ballerina run configuration type",
                NotNullLazyValue.createValue(() -> AllIcons.Nodes.Console));
        addFactory(new BallerinaApplicationRunConfigurationFactory(this));
    }

    @Override
    public Icon getIcon() {
        return BallerinaIcons.APPLICATION_RUN;
    }
}
