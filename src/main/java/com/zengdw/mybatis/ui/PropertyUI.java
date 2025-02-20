package com.zengdw.mybatis.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.ex.FileChooserDialogImpl;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.VirtualFile;
import com.zengdw.mybatis.vo.MyModule;
import com.zengdw.mybatis.vo.PropertyVO;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.File;
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
    private JCheckBox java8Date;
    private JCheckBox exampleQueryCheckBox;
    private JCheckBox lombokCheckBox;
    private JCheckBox serializableCheckBox;
    private JCheckBox mergeFileCheckBox;
    private JCheckBox blobBox;
    private JCheckBox isInt;
    private final Project project;
    private final Module[] moduleList;

    public PropertyUI(Project project, Module[] moduleList) {
        this.project = project;
        this.moduleList = moduleList;

        btnAddAction();
        moduleComboBox.addItemListener(e -> {
            javaModelPackage.setText("");
            mapperPackage.setText("");
            mapperXmlPackage.setText("");
        });
        exampleQueryCheckBox.addChangeListener(e -> {
            if (exampleQueryCheckBox.isSelected()) {
                blobBox.setEnabled(true);
            } else {
                blobBox.setEnabled(false);
                blobBox.setSelected(false);
            }
        });
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
        String currentDirectoryPath = virtualFile.getPresentableUrl().replace(File.separator, "/");
        currentDirectoryPath = disposeModulePath(currentDirectoryPath, prefix);

        FileChooserDescriptor chooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
        chooserDescriptor.withFileFilter(file -> file.isDirectory() && ModuleUtil.moduleContainsFile(selectModule.getModule(), file, false))
                .withRoots(virtualFile);
        FileChooserDialogImpl fileChooserDialog = new FileChooserDialogImpl(chooserDescriptor, project);
        VirtualFile[] files = fileChooserDialog.choose(project);
        if (files.length == 0) return null;
        String selectFilePath = files[0].getPresentableUrl().replace(File.separator, "/");
        selectFilePath = selectFilePath.replace(currentDirectoryPath + prefix, "");
        if (type == 1) {
            return selectFilePath.startsWith("/") ? selectFilePath.substring(1) : selectFilePath;
        }
        return selectFilePath.replace("/", ".").replaceFirst("\\.", "");
    }

    public static String disposeModulePath(String modulePath, String prePath) {
        if (StringUtils.isBlank(prePath)) return modulePath;
        String[] split = prePath.split("/");
        for (int i = split.length - 1; i >= 0; i--) {
            if (modulePath.endsWith("/")) {
                modulePath = modulePath.substring(0, modulePath.length() - 1);
            }
            if (modulePath.endsWith("/" + split[i])) {
                modulePath = modulePath.substring(0, modulePath.length() - split[i].length());
            }
        }
        if (!modulePath.endsWith("/")) modulePath = modulePath + "/";
        return modulePath;
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
        java8Date.setSelected(data.getJava8Date());
        exampleQueryCheckBox.setSelected(data.isExample());
        lombokCheckBox.setSelected(data.isLombok());
        serializableCheckBox.setSelected(data.isSerializable());
        blobBox.setSelected(data.getBlob());
        mergeFileCheckBox.setSelected(data.isMergeFile());
        isInt.setSelected(data.getInt());
    }

    public void getData(PropertyVO data) throws Exception {
        data.setModule((MyModule) moduleComboBox.getSelectedItem());
        data.setJavaModelPath(isEmpty(javaModelPath.getText()));
        data.setMapperPath(isEmpty(mapperPath.getText()));
        data.setMapperXmlPath(isEmpty(mapperXmlPath.getText()));
        data.setJavaModelPackage(isEmpty(javaModelPackage.getText()));
        data.setMapperPackage(isEmpty(mapperPackage.getText()));
        data.setMapperXmlPackage(isEmpty(mapperXmlPackage.getText()));
        data.setMapperAnnotation(mapperAnnotationCheckBox.isSelected());
        data.setComment(generateCommentCheckBox.isSelected());
        data.setJava8Date(java8Date.isSelected());
        data.setExample(exampleQueryCheckBox.isSelected());
        data.setLombok(lombokCheckBox.isSelected());
        data.setSerializable(serializableCheckBox.isSelected());
        data.setBlob(blobBox.isSelected());
        data.setMergeFile(mergeFileCheckBox.isSelected());
        data.setInt(isInt.isSelected());
    }

    private String isEmpty(String path) throws Exception {
        if (StringUtils.isBlank(path)) {
            throw new Exception("请配置包路径");
        }
        return path;
    }
}
