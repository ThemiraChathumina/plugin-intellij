package io.ballerina.plugins.idea.sdk;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Service(Service.Level.APP)
@State(name = "BallerinaSdkSettings", storages = @Storage("ballerinaSdkSettings.xml"))
public final class BallerinaSdkSettings implements PersistentStateComponent<BallerinaSdkSettings.State> {

    private State myState = new State();

    public static BallerinaSdkSettings getInstance() {
        return ServiceManager.getService(BallerinaSdkSettings.class);
    }

    @Nullable
    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.myState = state;
    }

    public String getBallerinaSdkPath() {
        return myState.ballerinaSdkPath;
    }

    public void setBallerinaSdkPath(String path) {
        myState.ballerinaSdkPath = path;
    }

    public String getBallerinaSdkVersion() {
        return myState.ballerinaSdkVersion;
    }

    public void setBallerinaSdkVersion(String version) {
        myState.ballerinaSdkVersion = version;
    }

    public boolean isUseCustomSdk() {
        return myState.useCustomSdk;
    }

    public void setUseCustomSdk(boolean useCustomSdk) {
        myState.useCustomSdk = useCustomSdk;
    }

    public static class State {

        public boolean useCustomSdk = false;
        public String ballerinaSdkPath = "";
        public String ballerinaSdkVersion = "";
    }
}
