package com.zengdw.mybatis.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.VisitableElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * @author zengd
 * @version 1.0
 * @date 2023/3/8 13:52
 */
public class EmptyStrPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        List<VisitableElement> elements = element.getElements();
        XmlElement xmlElement = (XmlElement) elements.get(2);
        XmlElement xmlElement1 = (XmlElement) elements.get(3);
        for (int i = 0; i < xmlElement.getElements().size(); i++) {
            XmlElement xmlEl = (XmlElement) xmlElement.getElements().get(i);
            XmlElement xmlEl1 = (XmlElement) xmlElement1.getElements().get(i);
            TextElement textElement = (TextElement) xmlEl.getElements().get(0);
            String colName = textElement.getContent();
            for (IntrospectedColumn column : allColumns) {
                boolean b = (column.getActualColumnName() + ",").equals(colName);
                b = b || (column.getJavaProperty() + ",").equals(colName);
                b = b && "String".equals(column.getFullyQualifiedJavaType().getShortName());
                if (b) {
                    Attribute attribute = xmlEl.getAttributes().get(0);
                    String value = attribute.getValue() + " and " + column.getJavaProperty() + " != ''";
                    Attribute attr = new Attribute(attribute.getName(), value);
                    xmlEl.getAttributes().remove(0);
                    xmlEl.getAttributes().add(0, attr);

                    Attribute attr1 = new Attribute(attribute.getName(), value);
                    xmlEl1.getAttributes().remove(0);
                    xmlEl1.getAttributes().add(0, attr1);
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public boolean sqlMapUpdateByExampleSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        addEmptyStrJudgment(element, introspectedTable);
        return true;
    }

    @Override
    public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return super.sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        addEmptyStrJudgment(element, introspectedTable);
        return true;
    }

    private static void addEmptyStrJudgment(XmlElement element, IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();

        XmlElement element1 = (XmlElement) element.getElements().get(2);
        for (int i = 0; i < element1.getElements().size(); i++) {
            XmlElement xmlEl = (XmlElement) element1.getElements().get(i);
            TextElement text = (TextElement) xmlEl.getElements().get(0);
            String colName = text.getContent().split("=")[0].trim();
            Attribute attr = xmlEl.getAttributes().get(0);

            for (IntrospectedColumn col : allColumns) {
                String actualColumnName = col.getActualColumnName();
                String typeName = col.getFullyQualifiedJavaType().getShortName();
                boolean b = actualColumnName.equals(colName);
                b = b && "String".equals(typeName);
                if (b) {
                    String value = attr.getValue() + " and " + attr.getValue().split("!=")[0].trim() + " != ''";
                    Attribute attr1 = new Attribute(attr.getName(), value);
                    xmlEl.getAttributes().remove(0);
                    xmlEl.getAttributes().add(attr1);
                    break;
                }
            }
        }
    }
}
