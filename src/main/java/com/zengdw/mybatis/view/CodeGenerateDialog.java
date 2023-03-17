package com.zengdw.mybatis.view;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author zengd
 * @version 1.0
 * @date 2023/3/14 9:26
 */
public class CodeGenerateDialog extends DialogWrapper {
    private final JPanel rootPanel = new JPanel();

    public CodeGenerateDialog(@Nullable Project project) {
        super(project);

        rootPanel.add(new PropertyPanel());

        super.init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }
}
