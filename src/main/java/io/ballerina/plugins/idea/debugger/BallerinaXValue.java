package io.ballerina.plugins.idea.debugger;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.ThreeState;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XInlineDebuggerDataCallback;
import com.intellij.xdebugger.frame.XNamedValue;
import com.intellij.xdebugger.frame.XNavigatable;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueModifier;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import com.intellij.xdebugger.frame.presentation.XRegularValuePresentation;
import com.intellij.xdebugger.frame.presentation.XStringValuePresentation;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import org.eclipse.lsp4j.debug.Variable;
import org.eclipse.lsp4j.debug.VariablesArguments;
import org.eclipse.lsp4j.debug.VariablesResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.intellij.openapi.editor.DefaultLanguageHighlighterColors.DOC_COMMENT;
import static com.intellij.openapi.editor.DefaultLanguageHighlighterColors.DOC_COMMENT_TAG;
import static com.intellij.openapi.editor.DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE;
import static com.intellij.openapi.editor.DefaultLanguageHighlighterColors.FUNCTION_DECLARATION;
import static com.intellij.openapi.editor.DefaultLanguageHighlighterColors.INSTANCE_METHOD;
import static com.intellij.openapi.editor.DefaultLanguageHighlighterColors.METADATA;
import static com.intellij.openapi.editor.DefaultLanguageHighlighterColors.STATIC_FIELD;
import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * Represents a value in the debug window.
 */
public class BallerinaXValue extends XNamedValue {

    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("BALLERINA_BAD_TOKEN", HighlighterColors.BAD_CHARACTER);
    public static final TextAttributesKey LINE_COMMENT =
            createTextAttributesKey("BALLERINA_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("BALLERINA_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey STRING =
            createTextAttributesKey("BALLERINA_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("BALLERINA_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey ANNOTATION = createTextAttributesKey("BALLERINA_ANNOTATION", METADATA);
    public static final TextAttributesKey PACKAGE = createTextAttributesKey("BALLERINA_PACKAGE", FUNCTION_DECLARATION);
    public static final TextAttributesKey TEMPLATE_LANGUAGE_COLOR =
            createTextAttributesKey("BALLERINA_TEMPLATE_LANGUAGE_COLOR", INSTANCE_METHOD);
    public static final TextAttributesKey DOCUMENTATION =
            createTextAttributesKey("BALLERINA_DOCUMENTATION", DOC_COMMENT);
    public static final TextAttributesKey DOCUMENTATION_VARIABLE =
            createTextAttributesKey("BALLERINA_DOCUMENTATION_VARIABLE", DOC_COMMENT_TAG);
    public static final TextAttributesKey DOCUMENTATION_INLINE_CODE =
            createTextAttributesKey("BALLERINA_DOCUMENTATION_INLINE_CODE", DOC_COMMENT_TAG_VALUE);
    public static final TextAttributesKey GLOBAL_VARIABLE =
            createTextAttributesKey("BALLERINA_GLOBAL_VARIABLE", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    public static final TextAttributesKey RECORD_KEY = createTextAttributesKey("BALLERINA_RECORD_KEY", STATIC_FIELD);
    public static final TextAttributesKey DEFAULT =
            createTextAttributesKey("BALLERINA_DEFAULT", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey ENTITY_NAME =
            createTextAttributesKey("BALLERINA_DATA_TYPE", FUNCTION_DECLARATION);
    private static final Logger LOG = Logger.getInstance(BallerinaXValue.class);
    @NotNull
    private final BallerinaDebugProcess process;
    @NotNull
    private final Variable variable;
    @Nullable
    private final Icon icon;

    public BallerinaXValue(@NotNull BallerinaDebugProcess process, @NotNull Variable variable, @Nullable Icon icon) {
        super(variable.getName());
        this.process = process;
        this.variable = variable;
        this.icon = icon;
    }

    @Nullable
    private static PsiElement findTargetElement(@NotNull Project project, @NotNull XSourcePosition position,
                                                @NotNull Editor editor, @NotNull String name) {
        // Todo
        return null;
    }

    private static void readActionInPooledThread(@NotNull Runnable runnable) {
        ApplicationManager.getApplication()
                .executeOnPooledThread(() -> ApplicationManager.getApplication().runReadAction(runnable));
    }

    public static Icon getIconFor(@NotNull Variable variable) {
        String variableType = variable.getType();
        if (BallerinaValueType.ARRAY.getValue().equals(variableType) ||
                BallerinaValueType.TUPLE.getValue().equals(variableType)) {
            return AllIcons.Debugger.Db_array;
        } else if (BallerinaValueType.OBJECT.getValue().equals(variableType) ||
                BallerinaValueType.RECORD.getValue().equals(variableType) ||
                BallerinaValueType.MAP.getValue().equals(variableType) ||
                BallerinaValueType.JSON.getValue().equals(variableType)) {
            return AllIcons.Debugger.Db_db_object;
        } else if (variableType.equals(BallerinaValueType.XML.getValue())) {
            return AllIcons.FileTypes.Xml;
        } else if (variableType.equals(BallerinaValueType.ERROR.getValue())) {
            return AllIcons.Nodes.ExceptionClass;
        } else {
            return AllIcons.Nodes.Variable;
        }
    }

    @Override
    public void computePresentation(@NotNull XValueNode node, @NotNull XValuePlace place) {
        XValuePresentation presentation = getPresentation();
        boolean hasChildren = variable.getVariablesReference() > 0;
        // check if variable.getVariablesReference() is null
        node.setPresentation(icon, presentation, hasChildren);
    }

    @Override
    public void computeChildren(@NotNull XCompositeNode node) {
        try {
            XValueChildrenList list = new XValueChildrenList();
            // Sends a variable request to get the child variables.
            VariablesArguments varArgs = new VariablesArguments();
            varArgs.setVariablesReference(variable.getVariablesReference());
            VariablesResponse variableResp = process.getDapClientConnector().getRequestManager().variables(varArgs);
            for (Variable variable : variableResp.getVariables()) {
                list.add(variable.getName(), new BallerinaXValue(process, variable, getIconFor(variable)));
            }
            node.addChildren(list, true);
        } catch (Exception e) {
            LOG.warn("Fetching DAP variable child values failed due to ", e);
        }
    }

    @Nullable
    @Override
    public XValueModifier getModifier() {
        return null;
    }

    @NotNull
    private XValuePresentation getPresentation() {
        String value = variable.getValue() != null ? variable.getValue() : "";
        String type = variable.getType() != null ? variable.getType() : "";

        if (type.equalsIgnoreCase(BallerinaValueType.STRING.getValue())) {
            // Trims leading and trailing double quotes, if presents.
            value = value.replaceAll("^\"+|\"+$", "");
            return new XStringValuePresentation(value);
        } else if (type.equalsIgnoreCase(BallerinaValueType.INT.getValue()) ||
                type.equalsIgnoreCase(BallerinaValueType.FLOAT.getValue()) ||
                type.equalsIgnoreCase(BallerinaValueType.DECIMAL.getValue())) {
            String finalValue = value;
            return new XRegularValuePresentation(finalValue, type) {
                @Override
                public void renderValue(@NotNull XValueTextRenderer renderer) {
                    renderer.renderValue(finalValue, NUMBER);
                }
            };
        } else if (type.equalsIgnoreCase(BallerinaValueType.BOOLEAN.getValue()) ||
                type.equalsIgnoreCase(BallerinaValueType.TYPE_DESC.getValue())) {
            String finalValue = value;
            return new XValuePresentation() {
                @Override
                public void renderValue(@NotNull XValueTextRenderer renderer) {
                    renderer.renderValue(finalValue, KEYWORD);
                }
            };
        } else if (type.equalsIgnoreCase(BallerinaValueType.XML.getValue())) {
            String finalValue = value;
            return new XValuePresentation() {
                @Nullable
                @Override
                public String getType() {
                    return type;
                }

                @Override
                public void renderValue(@NotNull XValueTextRenderer renderer) {
                    renderer.renderValue(finalValue, STRING);
                }
            };
        } else if (type.equalsIgnoreCase(BallerinaValueType.ERROR.getValue())) {
            String finalValue = value;
            return new XValuePresentation() {
                @Nullable
                @Override
                public String getType() {
                    return type;
                }

                @Override
                public void renderValue(@NotNull XValueTextRenderer renderer) {
                    renderer.renderError(finalValue);
                }
            };
        } else {
            return new XRegularValuePresentation(value, type);
        }
    }

    @Override
    public void computeSourcePosition(@NotNull XNavigatable navigatable) {
        readActionInPooledThread(new Runnable() {

            @Override
            public void run() {
                navigatable.setSourcePosition(findPosition());
            }

            @Nullable
            private XSourcePosition findPosition() {
                XDebugSession debugSession = process.getSession();
                if (debugSession == null) {
                    return null;
                }
                XStackFrame stackFrame = debugSession.getCurrentStackFrame();
                if (stackFrame == null) {
                    return null;
                }
                Project project = debugSession.getProject();
                XSourcePosition position = debugSession.getCurrentPosition();
                Editor editor =
                        ((FileEditorManagerImpl) FileEditorManager.getInstance(project)).getSelectedTextEditor(true);
                if (editor == null || position == null) {
                    return null;
                }
                String name = myName.startsWith("&") ? myName.replaceFirst("&", "") : myName;
                PsiElement resolved = findTargetElement(project, position, editor, name);
                if (resolved == null) {
                    return null;
                }
                VirtualFile virtualFile = resolved.getContainingFile().getVirtualFile();
                return XDebuggerUtil.getInstance().createPositionByOffset(virtualFile, resolved.getTextOffset());
            }
        });
    }

    @NotNull
    @Override
    public ThreeState computeInlineDebuggerData(@NotNull XInlineDebuggerDataCallback callback) {
        computeSourcePosition(callback::computed);
        return ThreeState.YES;
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }

    @Override
    public boolean canNavigateToTypeSource() {
        // Todo
        return false;
    }

    @Override
    public void computeTypeSourcePosition(@NotNull XNavigatable navigatable) {
        // Todo
    }
}
