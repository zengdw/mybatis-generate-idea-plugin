package com.zengdw.mybatis.plugins;

import com.zengdw.mybatis.vo.PropertyVO;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.VisitableElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.GeneratedKey;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengd
 * @date 2023/10/12 9:33
 */
@SuppressWarnings("unused")
public class MapperSelectivePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return !PropertyVO.of().isExample();
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        XmlElement answer = buildInitialInsert(introspectedTable);

        StringBuilder sb = new StringBuilder();

        sb.append("insert into "); //$NON-NLS-1$
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        XmlElement insertTrimElement = new XmlElement("trim"); //$NON-NLS-1$
        insertTrimElement.addAttribute(new Attribute("prefix", "(")); //$NON-NLS-1$ //$NON-NLS-2$
        insertTrimElement.addAttribute(new Attribute("suffix", ")")); //$NON-NLS-1$ //$NON-NLS-2$
        insertTrimElement.addAttribute(new Attribute("suffixOverrides", ",")); //$NON-NLS-1$ //$NON-NLS-2$
        answer.addElement(insertTrimElement);

        XmlElement valuesTrimElement = new XmlElement("trim"); //$NON-NLS-1$
        valuesTrimElement.addAttribute(new Attribute("prefix", "values (")); //$NON-NLS-1$ //$NON-NLS-2$
        valuesTrimElement.addAttribute(new Attribute("suffix", ")")); //$NON-NLS-1$ //$NON-NLS-2$
        valuesTrimElement.addAttribute(new Attribute("suffixOverrides", ",")); //$NON-NLS-1$ //$NON-NLS-2$
        answer.addElement(valuesTrimElement);

        for (IntrospectedColumn introspectedColumn :
                ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns())) {

            if (introspectedColumn.isSequenceColumn()
                    || introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
                // if it is a sequence column, it is not optional
                // This is required for MyBatis3 because MyBatis3 parses
                // and calculates the SQL before executing the selectKey

                // if it is primitive, we cannot do a null check
                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
                sb.append(',');
                insertTrimElement.addElement(new TextElement(sb.toString()));

                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
                sb.append(',');
                valuesTrimElement.addElement(new TextElement(sb.toString()));

                continue;
            }

            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" != null"); //$NON-NLS-1$
            XmlElement insertNotNullElement = new XmlElement("if"); //$NON-NLS-1$
            insertNotNullElement.addAttribute(new Attribute(
                    "test", sb.toString())); //$NON-NLS-1$

            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(',');
            insertNotNullElement.addElement(new TextElement(sb.toString()));
            insertTrimElement.addElement(insertNotNullElement);

            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" != null"); //$NON-NLS-1$
            XmlElement valuesNotNullElement = new XmlElement("if"); //$NON-NLS-1$
            valuesNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$

            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            sb.append(',');
            valuesNotNullElement.addElement(new TextElement(sb.toString()));
            valuesTrimElement.addElement(valuesNotNullElement);
        }

        List<VisitableElement> elements = element.getElements();
        elements.clear();
        elements.addAll(answer.getElements());
        List<Attribute> attributes = element.getAttributes();
        attributes.clear();
        attributes.addAll(answer.getAttributes());
        return true;
    }


    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        XmlElement answer = new XmlElement("update"); //$NON-NLS-1$

        answer.addAttribute(new Attribute(
                "id", "updateByPrimaryKey")); //$NON-NLS-1$

        String parameterType;

        parameterType = introspectedTable.getBaseRecordType();

        answer.addAttribute(new Attribute("parameterType", parameterType)); //$NON-NLS-1$

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();

        sb.append("update "); //$NON-NLS-1$
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        XmlElement dynamicElement = new XmlElement("set"); //$NON-NLS-1$
        answer.addElement(dynamicElement);

        for (IntrospectedColumn introspectedColumn :
                ListUtilities.removeGeneratedAlwaysColumns(introspectedTable.getNonPrimaryKeyColumns())) {
            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" != null"); //$NON-NLS-1$
            XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
            isNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
            dynamicElement.addElement(isNotNullElement);

            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = "); //$NON-NLS-1$
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            sb.append(',');

            isNotNullElement.addElement(new TextElement(sb.toString()));
        }

        buildPrimaryKeyWhereClause(introspectedTable).forEach(answer::addElement);

        List<Attribute> attributes = element.getAttributes();
        attributes.clear();
        attributes.addAll(answer.getAttributes());
        List<VisitableElement> elements = element.getElements();
        elements.clear();
        elements.addAll(answer.getElements());
        return true;
    }

    protected List<TextElement> buildPrimaryKeyWhereClause(IntrospectedTable introspectedTable) {
        List<TextElement> answer = new ArrayList<>();
        boolean first = true;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            String line;
            if (first) {
                line = "where "; //$NON-NLS-1$
                first = false;
            } else {
                line = "  and "; //$NON-NLS-1$
            }

            line += MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            line += " = "; //$NON-NLS-1$
            line += MyBatis3FormattingUtilities.getParameterClause(introspectedColumn);
            answer.add(new TextElement(line));
        }

        return answer;
    }

    protected XmlElement buildInitialInsert(IntrospectedTable introspectedTable) {
        XmlElement answer = new XmlElement("insert"); //$NON-NLS-1$

        answer.addAttribute(new Attribute("id", "insert")); //$NON-NLS-1$

        answer.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType())); //$NON-NLS-1$

        context.getCommentGenerator().addComment(answer);

        introspectedTable.getGeneratedKey().ifPresent(gk ->
                introspectedTable.getColumn(gk.getColumn()).ifPresent(introspectedColumn -> {
                    // if the column is null, then it's a configuration error. The
                    // warning has already been reported
                    if (gk.isJdbcStandard()) {
                        answer.addAttribute(new Attribute("useGeneratedKeys", "true")); //$NON-NLS-1$ //$NON-NLS-2$
                        answer.addAttribute(
                                new Attribute("keyProperty", introspectedColumn.getJavaProperty())); //$NON-NLS-1$
                        answer.addAttribute(
                                new Attribute("keyColumn", introspectedColumn.getActualColumnName())); //$NON-NLS-1$
                    } else {
                        answer.addElement(getSelectKey(introspectedColumn, gk));
                    }
                })
        );

        return answer;
    }

    protected XmlElement getSelectKey(IntrospectedColumn introspectedColumn,
                                      GeneratedKey generatedKey) {
        String identityColumnType = introspectedColumn.getFullyQualifiedJavaType().getFullyQualifiedName();

        XmlElement answer = new XmlElement("selectKey"); //$NON-NLS-1$
        answer.addAttribute(new Attribute("resultType", identityColumnType)); //$NON-NLS-1$
        answer.addAttribute(new Attribute(
                "keyProperty", introspectedColumn.getJavaProperty())); //$NON-NLS-1$
        answer.addAttribute(new Attribute("order", generatedKey.getMyBatis3Order())); //$NON-NLS-1$

        answer.addElement(new TextElement(generatedKey.getRuntimeSqlStatement()));

        return answer;
    }
}
