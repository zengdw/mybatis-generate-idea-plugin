package com.zengdw.mybatis.intention;

import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author zengd
 * @date 2024/2/23 15:01
 */
public class SelectIntentionAction extends AbstractIntentionAction {
    @Override
    public @IntentionName @NotNull String getText() {
        return "generate select method";
    }

    @Override
    protected String xmlTagStr(PsiMethod method) {
        PsiParameterList parameterList = method.getParameterList();
        int parametersCount = parameterList.getParametersCount();
        String xmlTagStr = "<select id=\"" + method.getName() + "\" ";
        if (parametersCount == 1) {
            String parameterType = parameterList.getParameter(0).getType().getCanonicalText();
            xmlTagStr += "parameterType=\""+parameterType+"\"";
        }
        xmlTagStr += " resultMap=\"BaseResultMap\">\n\n</select>";
        return xmlTagStr;
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) {
        boolean available = super.isAvailable(project, editor, psiElement);
        if (!available) return false;
        PsiMethod psiMethod = PsiTreeUtil.getParentOfType(psiElement, PsiMethod.class);
        String name = psiMethod.getName();
        return methodStart(name, "select") || (!methodStart(name, "update") && !methodStart(name, "delete") && !methodStart(name, "insert"));
    }
}
