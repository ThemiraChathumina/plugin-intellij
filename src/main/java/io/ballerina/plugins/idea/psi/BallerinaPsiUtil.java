package io.ballerina.plugins.idea.psi;

import com.intellij.psi.PsiElement;

public class BallerinaPsiUtil {
    public static PsiElement getPreviousSibling(PsiElement element) {
        if (element == null) {
            return null;
        }
        PsiElement prevSibling = element.getPrevSibling();
        while (prevSibling != null && prevSibling.toString().equals("PsiWhiteSpace")) {
            prevSibling = prevSibling.getPrevSibling();
        }
        return prevSibling;
    }

    public static PsiElement getNextSibling(PsiElement element) {
        if (element == null) {
            return null;
        }
        PsiElement nextSibling = element.getNextSibling();
        while (nextSibling != null && nextSibling.toString().equals("PsiWhiteSpace")) {
            nextSibling = nextSibling.getNextSibling();
        }
        return nextSibling;
    }
}
