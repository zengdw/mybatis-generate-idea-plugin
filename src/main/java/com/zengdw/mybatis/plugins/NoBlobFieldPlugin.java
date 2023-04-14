package com.zengdw.mybatis.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.VisitableElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.*;

import static org.mybatis.generator.internal.util.JavaBeansUtil.*;
import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

/**
 * BLOB CLOB 大字段 不单独生成实体类
 *
 * @author zengd
 * @version 1.0
 * @date 2023/4/7 14:40
 */
@SuppressWarnings("SpellCheckingInspection")
public class NoBlobFieldPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Plugin plugins = context.getPlugins();
        for (IntrospectedColumn introspectedColumn : introspectedTable.getBLOBColumns()) {
            Field field = getJavaBeansField(introspectedColumn, context, introspectedTable);
            if (plugins.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable,
                    Plugin.ModelClassType.BASE_RECORD)) {
                topLevelClass.addField(field);
                topLevelClass.addImportedType(field.getType());
            }

            Method method = getJavaBeansGetter(introspectedColumn, context, introspectedTable);
            if (plugins.modelGetterMethodGenerated(method, topLevelClass, introspectedColumn, introspectedTable,
                    Plugin.ModelClassType.BASE_RECORD)) {
                topLevelClass.addMethod(method);
            }

            if (!introspectedTable.isImmutable()) {
                method = getJavaBeansSetter(introspectedColumn, context, introspectedTable);
                if (plugins.modelSetterMethodGenerated(method, topLevelClass, introspectedColumn, introspectedTable,
                        Plugin.ModelClassType.BASE_RECORD)) {
                    topLevelClass.addMethod(method);
                }
            }
        }
        return true;
    }

    @Override
    public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapBaseColumnListElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> blobColumns = introspectedTable.getBLOBColumns();
        if (blobColumns.size() > 1) {
            List<TextElement> textElements = buildSelectList(blobColumns);
            textElements.forEach(element::addElement);
        }
        return true;
    }

    @Override
    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    protected List<TextElement> buildSelectList(List<IntrospectedColumn> columns) {
        List<TextElement> answer = new ArrayList<>();
        StringBuilder sb = new StringBuilder(", ");
        Iterator<IntrospectedColumn> iter = columns.iterator();

        while (iter.hasNext()) {
            sb.append(MyBatis3FormattingUtilities.getSelectListPhrase(iter.next()));
            if (iter.hasNext()) {
                sb.append(", ");
            }

            if (sb.length() > 80) {
                answer.add(new TextElement(sb.toString()));
                sb.setLength(0);
            }
        }

        if (sb.length() > 0) {
            answer.add(new TextElement(sb.toString()));
        }

        return answer;
    }

    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<VisitableElement> elements = element.getElements();
        elements.remove(3);
        elements.remove(3);
        Attribute attribute = new Attribute("resultMap", "BaseResultMap");
        element.getAttributes().set(1, attribute);
        return true;
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        updateParameterType(element, introspectedTable);
        return true;
    }

    @Override
    public boolean sqlMapUpdateByExampleWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        updateParameterType(element, introspectedTable);
        return true;
    }

    private void updateParameterType(XmlElement element, IntrospectedTable introspectedTable) {
        if (introspectedTable.getBLOBColumns().size() <= 1) return;
        FullyQualifiedJavaType fullyQualifiedJavaType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        Attribute attribute = new Attribute("parameterType", fullyQualifiedJavaType.getFullyQualifiedName());
        element.getAttributes().set(1, attribute);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return addUpdateBlobField(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return addUpdateBlobField(element, introspectedTable);
    }

    private boolean addUpdateBlobField(XmlElement element, IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> blobColumns = introspectedTable.getBLOBColumns();
        StringBuilder sb = new StringBuilder("  ");
        Iterator<IntrospectedColumn> iter = blobColumns.iterator();

        List<VisitableElement> elements = element.getElements();
        int i = elements.size() - 2;
        TextElement textElement = (TextElement) elements.get(i);
        TextElement newTextElement = new TextElement(textElement.getContent() + ",");
        elements.set(i, newTextElement);
        int index = elements.size() - 1;
        while (iter.hasNext()) {
            IntrospectedColumn introspectedColumn = iter.next();
            sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "row."));
            if (iter.hasNext()) {
                sb.append(',');
            }

            element.addElement(index++, new TextElement(sb.toString()));
            if (iter.hasNext()) {
                sb.setLength(0);
                OutputUtilities.xmlIndent(sb, 1);
            }
        }
        return true;
    }

    private boolean addBlobField(XmlElement element, IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> columns = introspectedTable.getBLOBColumns();
        buildResultMapItems(columns).forEach(element::addElement);
        return true;
    }

    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        updateParameterType(element, introspectedTable);
        return true;
    }

    @Override
    public boolean sqlMapBlobColumnListElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapResultMapWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapResultMapWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return addBlobField(element, introspectedTable);
    }

    @Override
    public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return replaceInsertParameter(method, interfaze, introspectedTable);
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        if (introspectedTable.getBLOBColumns().size() <= 1) return true;
        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        method.setReturnType(returnType);
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();
        importedTypes.add(returnType);
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
        return false;
    }

    @Override
    public boolean clientUpdateByExampleSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        if (introspectedTable.getBLOBColumns().size() <= 1) return true;
        List<Parameter> parameters = method.getParameters();
        parameters.remove(0);
        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        method.addParameter(0, new Parameter(parameterType, "row", "@Param(\"row\")")); //$NON-NLS-1$ //$NON-NLS-2$

        Set<FullyQualifiedJavaType> importedTypes = interfaze.getImportedTypes();
        importedTypes.add(parameterType);
        importedTypes.add(new FullyQualifiedJavaType(introspectedTable.getExampleType()));
        importedTypes.add(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        if (introspectedTable.getBLOBColumns().size() <= 1) return true;
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();
        List<Parameter> parameters = method.getParameters();
        parameters.remove(0);
        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        method.addParameter(new Parameter(parameterType, "row"));
        importedTypes.add(parameterType);
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
        return false;
    }

    private static boolean replaceInsertParameter(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        if (introspectedTable.getBLOBColumns().size() <= 1) return true;
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        method.getParameters().remove(0);
        method.addParameter(new Parameter(type, "row"));
        interfaze.addMethod(method);
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();
        importedTypes.add(type);
        interfaze.addImportedTypes(importedTypes);
        return false;
    }

    @Override
    public boolean clientInsertSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return replaceInsertParameter(method, interfaze, introspectedTable);
    }

    protected List<XmlElement> buildResultMapItems(List<IntrospectedColumn> columns) {
        List<XmlElement> answer = new ArrayList<>();
        for (IntrospectedColumn introspectedColumn : columns) {
            XmlElement resultElement = new XmlElement("result");

            resultElement.addAttribute(buildColumnAttribute(introspectedColumn));
            resultElement.addAttribute(new Attribute("property", introspectedColumn.getJavaProperty())); //$NON-NLS-1$
            resultElement.addAttribute(new Attribute("jdbcType", introspectedColumn.getJdbcTypeName())); //$NON-NLS-1$

            if (stringHasValue(introspectedColumn.getTypeHandler())) {
                resultElement.addAttribute(
                        new Attribute("typeHandler", introspectedColumn.getTypeHandler())); //$NON-NLS-1$
            }

            answer.add(resultElement);
        }

        return answer;
    }

    protected Attribute buildColumnAttribute(IntrospectedColumn introspectedColumn) {
        return new Attribute("column", //$NON-NLS-1$
                MyBatis3FormattingUtilities.getRenamedColumnNameForResultMap(introspectedColumn));
    }

}
