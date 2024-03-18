// This is a generated file. Not intended for manual editing.
package io.ballerina.plugins.idea.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.ballerina.plugins.idea.psi.*;

public class BallerinaGroupingKeyImpl extends ASTWrapperPsiElement implements BallerinaGroupingKey {

  public BallerinaGroupingKeyImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull BallerinaVisitor visitor) {
    visitor.visitGroupingKey(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BallerinaVisitor) accept((BallerinaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public BallerinaExpression getExpression() {
    return findChildByClass(BallerinaExpression.class);
  }

  @Override
  @Nullable
  public BallerinaInferableTypeDescriptor getInferableTypeDescriptor() {
    return findChildByClass(BallerinaInferableTypeDescriptor.class);
  }

  @Override
  @NotNull
  public BallerinaVariableName getVariableName() {
    return findNotNullChildByClass(BallerinaVariableName.class);
  }

}
