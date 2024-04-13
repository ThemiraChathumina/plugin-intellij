package org.intellij.sdk.language.debugger;


import com.intellij.icons.AllIcons;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueGroup;
import org.eclipse.lsp4j.debug.Variable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.swing.Icon;

import static org.intellij.sdk.language.debugger.BallerinaXValue.getIconFor;

/**
 * Represents a variable group in debug window.
 */
public class BallerinaXValueGroup extends XValueGroup {

    private final BallerinaDebugProcess process;
    private final List<Variable> variables;

    BallerinaXValueGroup(@NotNull BallerinaDebugProcess myProcess,
                         @NotNull String name, @NotNull List<Variable> myVariables) {
        super(name);
        this.variables = myVariables;
        this.process = myProcess;
    }

    @Override
    public boolean isRestoreExpansion() {
        return true;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return AllIcons.Debugger.Value;
    }

    @Override
    public void computeChildren(@NotNull XCompositeNode node) {
        if (variables.isEmpty()) {
            super.computeChildren(node);
        } else {
            XValueChildrenList list = new XValueChildrenList();
            for (Variable variable : variables) {
                list.add(variable.getName(), new BallerinaXValue(process, variable, getIconFor(variable)));
            }
            node.addChildren(list, true);
        }
    }
}
