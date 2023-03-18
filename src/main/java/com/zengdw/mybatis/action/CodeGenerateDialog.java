package com.zengdw.mybatis.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.zengdw.mybatis.ui.PropertyUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author zengd
 * @version 1.0
 * @date 2023/3/14 9:26
 */
public class CodeGenerateDialog extends DialogWrapper {
    private final JPanel rootPanel = new JPanel();
    private final PropertyUI propertyUI;
    private Action previous;
    private Action next;

    public CodeGenerateDialog(AnActionEvent e) {
        super(e.getProject());

        Module[] modules = ModuleManager.getInstance(e.getProject()).getModules();
        propertyUI = new PropertyUI(e.getProject(), modules);
        setResizable(false);
        rootPanel.add(propertyUI.getRootPanel());
        createAction();

        disableAction(previous, false);
        super.init();
    }

    private void disableAction(Action action, boolean enabled) {
        action.setEnabled(enabled);
    }

    private void createAction() {
        previous = new DialogWrapperAction("Previous") {
            @Override
            protected void doAction(ActionEvent e) {
                System.out.println("previous");
            }
        };
        next = new DialogWrapperAction("Next") {
            @Override
            protected void doAction(ActionEvent e) {
                System.out.println("next");
            }
        };
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{previous, next, getCancelAction()};
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }
}
