package org.intellij.sdk.language;

import com.intellij.openapi.projectRoots.*;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BallerinaSdkType extends SdkType {
    public BallerinaSdkType() {
        super("Ballerina SDK");
    }

    @NotNull
    public static BallerinaSdkType getInstance() {
        return SdkType.findInstance(BallerinaSdkType.class);
    }

    @Override
    public @Nullable String suggestHomePath() {
        // Suggest the Ballerina home path
        return BallerinaSdkService.getInstance().getBallerinaPath();
    }

    @Override
    public boolean isValidSdkHome(@NotNull String path) {
        // Check if the provided path is a valid Ballerina SDK home
        return true;
    }

    @Override
    public @NotNull String suggestSdkName(@Nullable String currentSdkName, @NotNull String sdkHome) {
        // Suggest a name for the SDK based on the Ballerina version
        return BallerinaSdkService.getInstance().getBallerinaVersion();
    }

    @Override
    public boolean setupSdkPaths(@NotNull Sdk sdk, @NotNull SdkModel sdkModel) {
        SdkModificator modificator = sdk.getSdkModificator();
        modificator.setVersionString(getVersionString(sdk));
        modificator.commitChanges(); // save
        return true;
    }

    @Override
    public @Nullable AdditionalDataConfigurable createAdditionalDataConfigurable(@NotNull SdkModel sdkModel, @NotNull SdkModificator sdkModificator) {
        // Create an additional data configurable for the SDK
        return null;
    }

    @Override
    public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getPresentableName() {
        return BallerinaSdkService.getInstance().getBallerinaVersion();
    }


    @Override
    public void saveAdditionalData(@NotNull SdkAdditionalData sdkAdditionalData, @NotNull Element element) {

    }
}