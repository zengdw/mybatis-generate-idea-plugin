package com.zengdw.mybatis.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.zengdw.mybatis.ui.PropertyUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author zengd
 * @version 1.0
 * @date 2023/3/14 9:26
 */
public class CodeGenerateDialog extends DialogWrapper {
    private final JPanel rootPanel;

    public CodeGenerateDialog(AnActionEvent e) {
        super(e.getProject());
        setResizable(false);

        Module[] modules = ModuleManager.getInstance(e.getProject()).getModules();
        PropertyUI propertyUI = new PropertyUI(e.getProject(), modules);
        rootPanel = propertyUI.getRootPanel();

        super.init();
    }

    @Override
    protected void doOKAction() {

        super.doOKAction();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }
}
