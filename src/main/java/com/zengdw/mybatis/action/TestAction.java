package com.zengdw.mybatis.action;

import com.intellij.database.model.DasTable;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbNamespaceImpl;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.JBIterable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        // 获取所有表
        JBIterable<? extends DasTable> tables = DasUtil.getTables((DbDataSource) psiElement.getParent());
        List<? extends DasTable> allTableList = tables.toList().stream().filter(t -> t.getDasParent().getName().equals(schema)).collect(Collectors.toList());

        CodeGenerateDialog dialog = new CodeGenerateDialog(e);
        dialog.show();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiElement[] elements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        boolean visible = elements != null && elements.length > 0 && Stream.of(elements).anyMatch(el -> DbTable.class.isAssignableFrom(el.getClass()));
        // 设置按钮隐藏
        e.getPresentation().setEnabledAndVisible(visible);
    }
}
