package com.zengdw.mybatis.intention;

import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import org.jetbrains.annotations.NotNull;

/**
 * @author zengd
 * @date 2024/2/23 15:00
 */
public class InsertIntentionAction extends AbstractIntentionAction {

    @Override
    public @IntentionName @NotNull String getText() {
        return "generate insert method";
    }

    @Override
    protected String xmlTagStr(PsiMethod method) {
        PsiParameterList parameterList = method.getParameterList();
        int parametersCount = parameterList.getParametersCount();
        String xmlTagStr = "<insert id=\"" + method.getName() + "\" ";
        if (parametersCount == 1) {
//            parameterList.getParameters()[0]
            xmlTagStr += "parameterType=\"\"";
        }
        xmlTagStr += ">\n</insert>";
        return xmlTagStr;
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) {
        boolean available = super.isAvailable(project, editor, psiElement);
        if (!available) return false;
        PsiMethod method = (PsiMethod) psiElement.getParent();
        String name = method.getName();
        return methodStart(name, "insert") || (!methodStart(name, "update") && !methodStart(name, "delete") && !methodStart(name, "select"));
    }
}
