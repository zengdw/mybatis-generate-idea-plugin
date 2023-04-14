package com.zengdw.mybatis.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.zengdw.mybatis.generate.GenerateCode;
import com.zengdw.mybatis.ui.PropertyUI;
import com.zengdw.mybatis.vo.PropertyVO;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.nio.charset.StandardCharsets;

/**
 * @author zengd
 * @version 1.0
 * @date 2023/3/14 9:26
 */
public class CodeGenerateDialog extends DialogWrapper {
    private final JPanel rootPanel;
    private final PropertyUI propertyUI;

    public CodeGenerateDialog(AnActionEvent e) {
        super(e.getProject());
        setResizable(false);

        Module[] modules = ModuleManager.getInstance(e.getProject()).getModules();
        propertyUI = new PropertyUI(e.getProject(), modules);
        propertyUI.setData(PropertyVO.of());
        rootPanel = propertyUI.getRootPanel();

        super.init();
    }

    @Override
    protected void doOKAction() {
        try {
            propertyUI.getData(PropertyVO.of());

            new GenerateCode().generate();
            VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
            Messages.showMessageDialog("success", "Tips", null);
            super.doOKAction();
        } catch (Exception e) {
            Messages.showMessageDialog(new String(e.getMessage().getBytes(), StandardCharsets.UTF_8), "Error", null);
        }
    }

    @Override
    public void doCancelAction() {
        try {
            super.doCancelAction();
        } catch (Exception e) {
            Messages.showMessageDialog(e.getMessage(), "Error", null);
        }
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }
}
