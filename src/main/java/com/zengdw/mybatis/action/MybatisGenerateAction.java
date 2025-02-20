package com.zengdw.mybatis.action;

import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DbUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.JBIterable;
import com.zengdw.mybatis.util.DbToolsUtils;
import com.zengdw.mybatis.vo.PropertyVO;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zengd
 */
public class MybatisGenerateAction extends AnAction {

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void actionPerformed(AnActionEvent e) {
        // 获取选中的表
        PsiElement[] selectTableElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        List<DbTable> dbTables = Stream.of(selectTableElements).filter(t -> t instanceof DbTable).map(t -> (DbTable) t).collect(Collectors.toList());
        PropertyVO.of().setTableList(dbTables);
        JBIterable<DbDataSource> dataSources = DbUtil.getDataSources(e.getProject());
        for (DbDataSource dataSource : dataSources) {
            String dbType = DbToolsUtils.extractDatabaseTypeFromUrl(dataSource.getConnectionConfig().getUrl());
            if (!dbType.trim().isEmpty()) {
                PropertyVO.of().setDbType(dbType);
                break;
            }
        }

        CodeGenerateDialog dialog = new CodeGenerateDialog(e);
        dialog.show();
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 使用 ReadAction 包装 PSI 操作
        PsiElement[] elements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        boolean visible = elements != null && elements.length > 0 && Stream.of(elements).anyMatch(el -> DbTable.class.isAssignableFrom(el.getClass()));
        // 设置按钮隐藏
        e.getPresentation().setEnabledAndVisible(visible);
    }
}
