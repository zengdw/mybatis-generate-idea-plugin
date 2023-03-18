package com.zengdw.mybatis.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.ex.FileChooserDialogImpl;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;

/**
 * @author zengd
 * @date 2023/3/17 22:39
 */
public class PropertyUI {
    private JPanel rootPanel;
    private JComboBox<String> moduleComboBox;
    private JTextField javaModelPath;
    private JButton javaModelPathBtn;
    private JButton javaModelPackageBtn;
    private JTextField mapperPath;
    private JTextField mapperXmlPath;
    private JButton mapperPathBtn;
    private JButton mapperPackageBtn;
    private JButton mapperXmlPathBtn;
    private JButton mapperXmlPackageBtn;
    private JTextField mapperPackage;
    private JTextField mapperXmlPackage;
    private JTextField javaModelPackage;
    private final Project project;

    public PropertyUI(Project project, Module[] moduleList) {
        this.project = project;
        for (Module module : moduleList) {
            this.moduleComboBox.addItem(module.getName());
        }

        javaModelPathBtn.addActionListener(e -> {
            String path = btnAction(null);
            javaModelPath.setText(path);
        });
    }

    public JPanel getRootPanel() {
        return this.rootPanel;
    }

    private String btnAction(String prefix) {
        Object selectModule = this.moduleComboBox.getSelectedItem();
        String currentDirectoryPath = project.getBasePath();
        if (null != selectModule) {
            Module module = (Module) selectModule;
            currentDirectoryPath = module.getModuleFilePath();
        }

        FileChooserDescriptor chooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
        FileChooserDialogImpl fileChooserDialog = new FileChooserDialogImpl(chooserDescriptor, project);
        fileChooserDialog.setTitle("Java Model Path Select");
        VirtualFile[] files = fileChooserDialog.choose(project);
        String selectFilePath = files[0].getCanonicalPath();
        return selectFilePath.substring(currentDirectoryPath.length() + 1);
    }

}
