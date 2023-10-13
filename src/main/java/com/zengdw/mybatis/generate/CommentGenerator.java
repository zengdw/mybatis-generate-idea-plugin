package com.zengdw.mybatis.generate;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.internal.util.StringUtility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;


/**
 * 类 方法 字段 注释生成
 *
 * @author zengd
 * @version 1.0
 * @date 2022/4/6 14:28
 */
@SuppressWarnings("unused")
public class CommentGenerator implements org.mybatis.generator.api.CommentGenerator {

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        addTableRemarks(topLevelClass, introspectedTable, null);

        StringBuilder sb = new StringBuilder();
        topLevelClass.addJavaDocLine(" * ");
        sb.setLength(0);
        sb.append(" * @author ").append(System.getProperties().getProperty("user.name"));
        topLevelClass.addJavaDocLine(sb.toString());
        sb.setLength(0);
        sb.append(" * @date ");
        sb.append(getDateString());
        topLevelClass.addJavaDocLine(sb.toString());
        topLevelClass.addJavaDocLine(" */");
    }

    private void addTableRemarks(JavaElement topLevelClass, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        topLevelClass.addJavaDocLine("/**");

        String remarks;
        if (null != introspectedTable) {
            remarks = introspectedTable.getRemarks();
        } else {
            remarks = introspectedColumn.getRemarks();
        }

        if (StringUtility.stringHasValue(remarks)) {
            String[] remarkLines = remarks.split(System.getProperty("line.separator"));
            for (String remarkLine : remarkLines) {
                topLevelClass.addJavaDocLine(" * " + remarkLine);
            }
        }
    }

    @Override
    public void addConfigurationProperties(Properties properties) {

    }

    /**
     * 实体类字段注释生成
     */
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable,
                                IntrospectedColumn introspectedColumn) {
        addTableRemarks(field, null, introspectedColumn);

        addJavadocTag(field);

        field.addJavaDocLine(" */");
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        if ("serialVersionUID".equals(field.getName())) {
            field.addJavaDocLine("/**");

            addJavadocTag(field);

            field.addJavaDocLine(" */");
        }
    }

    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
        method.addJavaDocLine("/**");
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        sb.append(" * @return ");
        sb.append(introspectedColumn.getRemarks());
        method.addJavaDocLine(sb.toString());

        addJavadocTag(method);

        method.addJavaDocLine(" */");
    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
        method.addJavaDocLine("/**");
        StringBuilder sb = new StringBuilder();
        Parameter param = method.getParameters().get(0);
        sb.setLength(0);
        sb.append(" * @param ");
        sb.append(param.getName());
        sb.append(" ");
        sb.append(introspectedColumn.getRemarks());
        method.addJavaDocLine(sb.toString());

        addJavadocTag(method);

        method.addJavaDocLine(" */");
    }

    /**
     * example和mapper接口文件 方法注释生成
     */
    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        method.addJavaDocLine("/**");

        addJavadocTag(method);

        method.addJavaDocLine(" */");
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
        innerClass.addJavaDocLine("/**");
        addJavadocTag(innerClass);

        innerClass.addJavaDocLine(" */");
    }

    /**
     * mapper xml文件注释生成
     */
    @Override
    public void addComment(XmlElement xmlElement) {
        xmlElement.addElement(new TextElement("<!-- " + MergeConstants.NEW_ELEMENT_TAG + " -->"));
    }

    protected void addJavadocTag(JavaElement javaElement) {
        javaElement.addJavaDocLine(" *");
        String sb = " * " + MergeConstants.NEW_ELEMENT_TAG;
        javaElement.addJavaDocLine(sb);
    }

    protected String getDateString() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
