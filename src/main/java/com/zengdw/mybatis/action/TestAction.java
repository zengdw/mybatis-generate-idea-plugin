package com.zengdw.mybatis.action;

import com.intellij.database.model.DasTable;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbNamespaceImpl;
import com.intellij.database.util.DasUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.JBIterable;
import com.zengdw.mybatis.view.CodeGenerateDialog;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zengd
 */
public class TestAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiElement psiElement = e.getData(PlatformCoreDataKeys.PSI_ELEMENT).getParent();
        String schema = ((DbNamespaceImpl) psiElement).getName();
        // 获取选中的表
        PsiElement[] selectTableElements = e.getData(PlatformCoreDataKeys.PSI_ELEMENT_ARRAY);
        JBIterable<? extends DasTable> tables = DasUtil.getTables((DbDataSource) psiElement.getParent());
        List<? extends DasTable> allTableList = tables.toList().stream().filter(t -> t.getDasParent().getName().equals(schema)).collect(Collectors.toList());
        System.out.println(3);

        CodeGenerateDialog dialog = new CodeGenerateDialog(e.getProject());
        dialog.show();
    }

}
