package com.zengdw.mybatis.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.ex.FileChooserDialogImpl;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.VirtualFile;
import com.zengdw.mybatis.util.FileUtil;
import com.zengdw.mybatis.vo.MyModule;

import javax.swing.*;
import java.util.Arrays;

/**
 * @author zengd
 * @date 2023/3/17 22:39
 */
@SuppressWarnings("DataFlowIssue")
public class PropertyUI {
    private JPanel rootPanel;
    private JComboBox<MyModule> moduleComboBox;
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
    private JCheckBox mapperAnnotationCheckBox;
    private JCheckBox generateCommentCheckBox;
    private JCheckBox mybatisPlusCheckBox;
    private JCheckBox exampleQueryCheckBox;
    private JCheckBox lombokCheckBox;
    private JCheckBox serializableCheckBox;
    private JCheckBox trimStringCheckBox;
    private JCheckBox toStringCheckBox;
    private JCheckBox mergeFileCheckBox;
    private final Project project;
    private final Module[] moduleList;

    public PropertyUI(Project project, Module[] moduleList) {
        this.project = project;
        this.moduleList = moduleList;

        btnAddAction();
    }

    private void btnAddAction() {
        javaModelPathBtn.addActionListener(e -> {
            String path = btnAction("", 1);
            if (null != path) javaModelPath.setText(path);
        });
        javaModelPackageBtn.addActionListener(e -> {
            String path = btnAction(this.javaModelPath.getText(), 2);
            if (null != path) javaModelPackage.setText(path);
        });
        mapperPathBtn.addActionListener(e -> {
            String path = btnAction("", 1);
            if (null != path) mapperPath.setText(path);
        });
        mapperPackageBtn.addActionListener(e -> {
            String path = btnAction(this.mapperPath.getText(), 2);
            if (null != path) mapperPackage.setText(path);
        });
        mapperXmlPathBtn.addActionListener(e -> {
            String path = btnAction("", 1);
            if (null != path) mapperXmlPath.setText(path);
        });
        mapperXmlPackageBtn.addActionListener(e -> {
            String path = btnAction(this.mapperXmlPath.getText(), 2);
            if (null != path) mapperXmlPackage.setText(path);
        });
    }

    public JPanel getRootPanel() {
        return this.rootPanel;
    }

    private String btnAction(String prefix, int type) {
        Object selectModule = this.moduleComboBox.getSelectedItem();
        String currentDirectoryPath = project.getBasePath();
        if (null != selectModule) {
            MyModule module = (MyModule) selectModule;
            currentDirectoryPath = module.getPath();
        }
        currentDirectoryPath = FileUtil.separatorConversion(currentDirectoryPath);

        VirtualFile projectDir = ProjectUtil.guessProjectDir(project);

        FileChooserDescriptor chooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
        chooserDescriptor.withRoots(projectDir);
        FileChooserDialogImpl fileChooserDialog = new FileChooserDialogImpl(chooserDescriptor, project);
        VirtualFile[] files = fileChooserDialog.choose(project, projectDir);
        if (files.length == 0) return null;
        String selectFilePath = files[0].getPresentableUrl();
        selectFilePath = FileUtil.separatorConversion(selectFilePath);
        String path = selectFilePath.replace(currentDirectoryPath + FileUtil.SEPARATOR + prefix, "");
        if (type == 1) {
            return path;
        }
        return path.replaceAll(FileUtil.SEPARATOR, ".").replaceFirst("\\.", "");
    }

    private void createUIComponents() {
        this.moduleComboBox = new ComboBox<>(Arrays.stream(moduleList).map(m -> new MyModule(m.getName(), ProjectUtil.guessModuleDir(m).getPresentableUrl())).toArray(MyModule[]::new));
    }

}
