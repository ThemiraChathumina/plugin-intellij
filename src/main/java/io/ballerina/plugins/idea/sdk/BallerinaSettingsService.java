package io.ballerina.plugins.idea.sdk;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "BallerinaSettings",
        storages = @Storage("StoragePathMacros.WORKSPACE_FILE")
)
public class BallerinaSettingsService implements PersistentStateComponent<BallerinaSettingsService.State> {

    public static class State {
        public String ballerinaRuntimePath;
        public String ballerinaVersion;
    }

    private State myState = new State();

    public static BallerinaSettingsService getInstance(Project project) {
        return ServiceManager.getService(project, BallerinaSettingsService.class);
    }

    @Nullable
    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

    public String getBallerinaRuntimePath() {
        return myState.ballerinaRuntimePath;
    }

    public void setBallerinaRuntimePath(String path) {
        myState.ballerinaRuntimePath = path;
    }

    public String getBallerinaVersion() {
        return myState.ballerinaVersion;
    }

    public void setBallerinaVersion(String version) {
        myState.ballerinaVersion = version;
    }
}

