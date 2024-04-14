package org.intellij.sdk.language;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.CustomFoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.sdk.language.psi.BallerinaDocumentationString;
import org.intellij.sdk.language.psi.BallerinaFunctionDefnBody;
import org.intellij.sdk.language.psi.BallerinaImportDecl;
import org.intellij.sdk.language.psi.BallerinaMappingConstructorExpr;
import org.intellij.sdk.language.psi.BallerinaTokens;
import org.intellij.sdk.language.psi.BallerinaTokensIgnore;
import org.intellij.sdk.language.psi.BallerinaTypes;
import org.jetbrains.annotations.NotNull;
import psi.BallerinaFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

public class BallerinaFoldingBuilder extends CustomFoldingBuilder implements DumbAware {

    @Override
    protected void buildLanguageFoldRegions(@NotNull List<FoldingDescriptor> list, @NotNull PsiElement psiElement,
                                            @NotNull Document document, boolean b) {
        if (!(psiElement instanceof BallerinaFile)) {
            return;
        }
        matchPair(list,psiElement, BallerinaTypes.OPEN_BRACE_TOKEN, BallerinaTypes.CLOSE_BRACE_TOKEN);
        matchPair(list,psiElement, BallerinaTypes.OPEN_NESTED_BRACE_TOKEN, BallerinaTypes.CLOSE_NESTED_BRACE_TOKEN);
        matchPair(list,psiElement, BallerinaTypes.IGNORED_OPEN_BRACE_TOKEN, BallerinaTypes.IGNORED_CLOSE_BRACE_TOKEN);
        matchPair(list,psiElement, BallerinaTypes.OPEN_BRACE_PIPE_TOKEN, BallerinaTypes.CLOSE_BRACE_PIPE_TOKEN);
        matchPair(list,psiElement, BallerinaTypes.OPEN_NESTED_BRACE_PIPE_TOKEN, BallerinaTypes.CLOSE_NESTED_BRACE_PIPE_TOKEN);
        buildImportFoldingRegion(list, psiElement);
        buildDocumentationFoldingRegions(list,psiElement);
        buildMultiCommentFoldingRegions(list, psiElement);
//        buildFunctionFoldingRegion(list, psiElement);
//        buildAnnotFoldingRegions(list, psiElement);
    }


    private void matchPair(List<FoldingDescriptor> list, PsiElement psiElement, IElementType open, IElementType close){
        List<PsiElement> leaves = new ArrayList<>();
        Stack<PsiElement> stack = new Stack<>();
        stack.push(psiElement);

        while (!stack.isEmpty()) {
            PsiElement element = stack.pop();
            // If the element has no children, it's a leaf
            if (element.getFirstChild() == null
                    && (element.getNode().getElementType() == open
                        || element.getNode().getElementType() == close )) {
                leaves.add(0,element);
            } else {
                // Push all children to the stack
                PsiElement child = element.getFirstChild();
                while (child != null) {
                    stack.push(child);
                    child = child.getNextSibling();
                }
            }
        }
        Stack<PsiElement> openBraces = new Stack<>();
        for (PsiElement leaf : leaves) {
            if (leaf.getNode().getElementType() == open) {
                openBraces.push(leaf);
            } else if (leaf.getNode().getElementType() == close && !openBraces.isEmpty()) {
                PsiElement openBrace = openBraces.pop();
                int startOffset = openBrace.getTextRange().getStartOffset();
                int endOffset = leaf.getTextRange().getEndOffset();
                if (endOffset > startOffset){
                    list.add(new FoldingDescriptor(openBrace.getNode(), new TextRange(startOffset, endOffset), null,"{...}"));
                }
            }
        }
    }

    private void buildAnnotFoldingRegions(List<FoldingDescriptor> list, PsiElement psiElement) {
        Collection<BallerinaMappingConstructorExpr> mappingConstructorExprs =
                PsiTreeUtil.findChildrenOfType(psiElement, BallerinaMappingConstructorExpr.class);
        for (BallerinaMappingConstructorExpr mappingConstructorExpr : mappingConstructorExprs) {
            int startOffset = mappingConstructorExpr.getTextRange().getStartOffset();
            int endOffset = mappingConstructorExpr.getTextRange().getEndOffset();
            list.add(new FoldingDescriptor(mappingConstructorExpr, startOffset, endOffset, null, "{...}"));
        }
    }

    private void buildFunctionFoldingRegion(List<FoldingDescriptor> list, PsiElement psiElement) {
        Collection<BallerinaFunctionDefnBody> functionDefnBodies =
                PsiTreeUtil.findChildrenOfType(psiElement, BallerinaFunctionDefnBody.class);

        for (BallerinaFunctionDefnBody functionDefnBody : functionDefnBodies) {
            BallerinaTokens tokens = PsiTreeUtil.getChildOfType(functionDefnBody, BallerinaTokens.class);
            if (tokens == null) {
                BallerinaTokensIgnore tokensIgnore =
                        PsiTreeUtil.getChildOfType(functionDefnBody, BallerinaTokensIgnore.class);
                if (tokensIgnore == null) {
                    continue;
                }
                addFoldingDescriptor(list, functionDefnBody, tokensIgnore, true);
            }
            addFoldingDescriptor(list, functionDefnBody, tokens, true);
        }
    }

    private void buildImportFoldingRegion(List<FoldingDescriptor> list, PsiElement psiElement) {
        Collection<BallerinaImportDecl> importDeclarationNodes =
                PsiTreeUtil.findChildrenOfType(psiElement, BallerinaImportDecl.class);
        if (!importDeclarationNodes.isEmpty()) {
            BallerinaImportDecl[] importDecls = importDeclarationNodes.toArray(new BallerinaImportDecl[0]);
            BallerinaImportDecl firstImport = importDecls[0];
            BallerinaImportDecl lastImport = importDecls[importDeclarationNodes.size() - 1];
            int startOffset = firstImport.getTextRange().getEndOffset();
            int lastOffset = lastImport.getTextRange().getEndOffset();
            if (startOffset < lastOffset) {
                list.add(new FoldingDescriptor(firstImport, startOffset, lastOffset, null, "..."));
            }
        }
    }

    private void buildDocumentationFoldingRegions(@NotNull List<FoldingDescriptor> list,
                                                  @NotNull PsiElement root) {
        // Get all documentation nodes.
        Collection<BallerinaDocumentationString> docStrings = PsiTreeUtil.findChildrenOfType(root,
                BallerinaDocumentationString.class);
        for (BallerinaDocumentationString docString : docStrings) {
            if (docString != null) {
                // Calculate the start and end offsets.
                int startOffset = docString.getTextRange().getStartOffset();
                int endOffset = docString.getTextRange().getEndOffset();
                // Add the new folding descriptor.
                list.add(new FoldingDescriptor(docString, startOffset, endOffset, null, "# ..."));
            }
        }
    }

    private void buildMultiCommentFoldingRegions(@NotNull List<FoldingDescriptor> list,
                                                 @NotNull PsiElement root) {

        Collection<PsiComment> comments = PsiTreeUtil.findChildrenOfType(root, PsiComment.class);

        for (PsiComment comment : comments) {
            PsiElement prevSibling = getPreviousElement(comment);
            // Prevents adding sub folding regions inside the comment blocks.
            if (prevSibling instanceof PsiComment) {
                continue;
            }
            PsiElement lastElement = getNextElement(comment);
            // Prevents folding single line comments.
            if (!(lastElement instanceof PsiComment)) {
                continue;
            }
            PsiElement nextSibling = getNextElement(lastElement);
            while (nextSibling instanceof PsiComment) {
                lastElement = nextSibling;
                nextSibling = getNextElement(lastElement);
            }
            // Calculates the region of the multiline comment.
            int startOffset = comment.getTextRange().getStartOffset();
            int endOffset = lastElement.getTextRange().getEndOffset();

            // Add the new folding descriptor.
            list.add(new FoldingDescriptor(comment, startOffset, endOffset, null, "// ..."));
        }
    }

    private PsiElement getPreviousElement(PsiElement element) {
        PsiElement prev = element.getPrevSibling();
        while (prev instanceof PsiWhiteSpace) {
            prev = prev.getPrevSibling();
        }
        return prev;
    }

    private PsiElement getNextElement(PsiElement element) {
        PsiElement next = element.getNextSibling();
        while (next instanceof PsiWhiteSpace) {
            next = next.getNextSibling();
        }
        return next;
    }


    private void addFoldingDescriptor(@NotNull List<FoldingDescriptor> descriptors, PsiElement node,
                                      PsiElement bodyNode, boolean includePrevious) {

        if (bodyNode == null || node == null){
            return;
        }
        PsiElement startNode = bodyNode;
        if (includePrevious) {
            PsiElement prevSibling = bodyNode.getPrevSibling();
            // Sometimes the body node might start with a comment node.
            while ((prevSibling instanceof PsiComment || prevSibling instanceof PsiWhiteSpace)) {
                prevSibling = prevSibling.getPrevSibling();
            }
            startNode = prevSibling;
        }

        if (startNode != null) {
            // Calculate the start and end offsets.
            int startOffset = startNode.getTextRange().getStartOffset();
            int endOffset = node.getTextRange().getEndOffset();
            // Add the new folding descriptor.
            descriptors.add(new FoldingDescriptor(node, startOffset, endOffset, null, "{...}"));
        }
    }

    @Override
    protected String getLanguagePlaceholderText(@NotNull ASTNode astNode, @NotNull TextRange textRange) {
        return "...";
    }

    @Override
    protected boolean isRegionCollapsedByDefault(@NotNull ASTNode astNode) {
        return false;
    }
}
