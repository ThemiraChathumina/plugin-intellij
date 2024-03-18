// This is a generated file. Not intended for manual editing.
package io.ballerina.plugins.idea.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.*;
import io.ballerina.plugins.idea.psi.*;

public class BallerinaSequenceTypeDescriptorImpl extends BallerinaTypeDescriptorImpl implements BallerinaSequenceTypeDescriptor {

  public BallerinaSequenceTypeDescriptorImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull BallerinaVisitor visitor) {
    visitor.visitSequenceTypeDescriptor(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BallerinaVisitor) accept((BallerinaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public BallerinaStringTypeDescriptor getStringTypeDescriptor() {
    return findChildByClass(BallerinaStringTypeDescriptor.class);
  }

  @Override
  @Nullable
  public BallerinaXmlTypeDescriptor getXmlTypeDescriptor() {
    return findChildByClass(BallerinaXmlTypeDescriptor.class);
  }

}
