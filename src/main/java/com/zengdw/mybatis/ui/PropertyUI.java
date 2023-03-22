package com.zengdw.mybatis.ui;

import com.intellij.openapi.fileChooser.ex.FileChooserDialogImpl;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.VirtualFile;
import com.zengdw.mybatis.override.MyFileChooserDescriptor;
import com.zengdw.mybatis.vo.MyModule;
import com.zengdw.mybatis.vo.PropertyVO;

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
        MyModule selectModule = (MyModule) this.moduleComboBox.getSelectedItem();
        VirtualFile virtualFile = ProjectUtil.guessModuleDir(selectModule.getModule());
        String currentDirectoryPath = virtualFile.getPresentableUrl();

        MyFileChooserDescriptor chooserDescriptor = new MyFileChooserDescriptor(false, true, false, false, false, false);
        chooserDescriptor.withFolderFilter(file -> ModuleUtil.moduleContainsFile(selectModule.getModule(), file, false))
                .withRoots(virtualFile);
        FileChooserDialogImpl fileChooserDialog = new FileChooserDialogImpl(chooserDescriptor, project);
        VirtualFile[] files = fileChooserDialog.choose(project);
        if (files.length == 0) return null;
        String selectFilePath = files[0].getPresentableUrl();
        if (type == 1) {
            return selectFilePath;
        }
        return selectFilePath.replace(currentDirectoryPath + "/" + prefix, "").replaceAll("/", ".").replaceFirst("\\.", "");
    }

    private void createUIComponents() {
        this.moduleComboBox = new ComboBox<>(Arrays.stream(moduleList).map(m -> new MyModule(m.getName(), m)).toArray(MyModule[]::new));
    }

    public void setData(PropertyVO data) {
        if (null != data.getModule()) {
            moduleComboBox.setSelectedItem(data.getModule());
        }
        javaModelPath.setText(data.getJavaModelPath());
        mapperPath.setText(data.getMapperPath());
        mapperXmlPath.setText(data.getMapperXmlPath());
        javaModelPackage.setText(data.getJavaModelPackage());
        mapperPackage.setText(data.getMapperPackage());
        mapperXmlPackage.setText(data.getMapperXmlPackage());
        mapperAnnotationCheckBox.setSelected(data.isMapperAnnotation());
        generateCommentCheckBox.setSelected(data.isComment());
        mybatisPlusCheckBox.setSelected(data.isMybatisPlus());
        exampleQueryCheckBox.setSelected(data.isExample());
        lombokCheckBox.setSelected(data.isLombok());
        serializableCheckBox.setSelected(data.isSerializable());
        trimStringCheckBox.setSelected(data.isTrimString());
        toStringCheckBox.setSelected(data.isToString());
        mergeFileCheckBox.setSelected(data.isMergeFile());
    }

    public void getData(PropertyVO data) {
        data.setModule((MyModule) moduleComboBox.getSelectedItem());
        data.setJavaModelPath(javaModelPath.getText());
        data.setMapperPath(mapperPath.getText());
        data.setMapperXmlPath(mapperXmlPath.getText());
        data.setJavaModelPackage(javaModelPackage.getText());
        data.setMapperPackage(mapperPackage.getText());
        data.setMapperXmlPackage(mapperXmlPackage.getText());
        data.setMapperAnnotation(mapperAnnotationCheckBox.isSelected());
        data.setComment(generateCommentCheckBox.isSelected());
        data.setMybatisPlus(mybatisPlusCheckBox.isSelected());
        data.setExample(exampleQueryCheckBox.isSelected());
        data.setLombok(lombokCheckBox.isSelected());
        data.setSerializable(serializableCheckBox.isSelected());
        data.setTrimString(trimStringCheckBox.isSelected());
        data.setToString(toStringCheckBox.isSelected());
        data.setMergeFile(mergeFileCheckBox.isSelected());
    }
}
