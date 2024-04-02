package com.zengdw.mybatis.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 在example中生成blog clob类型的字段方法
 *
 * @author zengd
 * @date 2024/4/2 15:33
 */
public class BlobFiledExamplePlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        InnerClass criteriaInnerClass = getGeneratedCriteriaInnerClass(topLevelClass, introspectedTable);
        topLevelClass.getInnerClasses().set(0, criteriaInnerClass);

        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }

    private InnerClass getGeneratedCriteriaInnerClass(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        InnerClass answer = new InnerClass(FullyQualifiedJavaType.getGeneratedCriteriaInstance());
        answer.setVisibility(JavaVisibility.PROTECTED);
        answer.setStatic(true);
        answer.setAbstract(true);
        this.context.getCommentGenerator().addClassComment(answer, introspectedTable);
        Method method = new Method("GeneratedCriteria");
        method.setVisibility(JavaVisibility.PROTECTED);
        method.setConstructor(true);
        method.addBodyLine("super();");
        method.addBodyLine("criteria = new ArrayList<>();");
        answer.addMethod(method);
        List<String> criteriaLists = new ArrayList<>();
        criteriaLists.add("criteria");


        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            if (StringUtility.stringHasValue(introspectedColumn.getTypeHandler())) {
                String name = this.addTypeHandledObjectsAndMethods(introspectedColumn, method, answer);
                criteriaLists.add(name);
            }
        }

        method = new Method("isValid");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
        StringBuilder sb = new StringBuilder();
        Iterator<String> strIter = criteriaLists.iterator();
        sb.append("return ");
        sb.append(strIter.next());
        sb.append(".size() > 0");
        if (!strIter.hasNext()) {
            sb.append(';');
        }

        method.addBodyLine(sb.toString());

        for (; strIter.hasNext(); method.addBodyLine(sb.toString())) {
            sb.setLength(0);
            OutputUtilities.javaIndent(sb, 1);
            sb.append("|| ");
            sb.append(strIter.next());
            sb.append(".size() > 0");
            if (!strIter.hasNext()) {
                sb.append(';');
            }
        }

        answer.addMethod(method);
        Field field;
        if (criteriaLists.size() > 1) {
            field = new Field("allCriteria", new FullyQualifiedJavaType("List<Criterion>"));
            field.setVisibility(JavaVisibility.PROTECTED);
            answer.addField(field);
        }

        method = new Method("getAllCriteria");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("List<Criterion>"));
        if (criteriaLists.size() < 2) {
            method.addBodyLine("return criteria;");
        } else {
            method.addBodyLine("if (allCriteria == null) {");
            method.addBodyLine("allCriteria = new ArrayList<>();");
            strIter = criteriaLists.iterator();

            while (strIter.hasNext()) {
                method.addBodyLine(String.format("allCriteria.addAll(%s);", strIter.next()));
            }

            method.addBodyLine("}");
            method.addBodyLine("return allCriteria;");
        }

        answer.addMethod(method);
        topLevelClass.addImportedType(FullyQualifiedJavaType.getNewListInstance());
        topLevelClass.addImportedType(FullyQualifiedJavaType.getNewArrayListInstance());
        FullyQualifiedJavaType listOfCriterion = new FullyQualifiedJavaType("java.util.List<Criterion>");
        field = new Field("criteria", listOfCriterion);
        field.setVisibility(JavaVisibility.PROTECTED);
        answer.addField(field);
        method = new Method(JavaBeansUtil.getGetterMethodName(field.getName(), field.getType()));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(field.getType());
        method.addBodyLine("return criteria;");
        answer.addMethod(method);
        method = new Method("addCriterion");
        method.setVisibility(JavaVisibility.PROTECTED);
        method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition"));
        method.addBodyLine("if (condition == null) {");
        method.addBodyLine("throw new RuntimeException(\"Value for condition cannot be null\");");
        method.addBodyLine("}");
        method.addBodyLine("criteria.add(new Criterion(condition));");
        if (criteriaLists.size() > 1) {
            method.addBodyLine("allCriteria = null;");
        }

        answer.addMethod(method);
        method = new Method("addCriterion");
        method.setVisibility(JavaVisibility.PROTECTED);
        method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition"));
        method.addParameter(new Parameter(FullyQualifiedJavaType.getObjectInstance(), "value"));
        method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "property"));
        method.addBodyLine("if (value == null) {");
        method.addBodyLine("throw new RuntimeException(\"Value for \" + property + \" cannot be null\");");
        method.addBodyLine("}");
        method.addBodyLine("criteria.add(new Criterion(condition, value));");
        if (criteriaLists.size() > 1) {
            method.addBodyLine("allCriteria = null;");
        }

        answer.addMethod(method);
        method = new Method("addCriterion");
        method.setVisibility(JavaVisibility.PROTECTED);
        method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition"));
        method.addParameter(new Parameter(FullyQualifiedJavaType.getObjectInstance(), "value1"));
        method.addParameter(new Parameter(FullyQualifiedJavaType.getObjectInstance(), "value2"));
        method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "property"));
        method.addBodyLine("if (value1 == null || value2 == null) {");
        method.addBodyLine("throw new RuntimeException(\"Between values for \" + property + \" cannot be null\");");
        method.addBodyLine("}");
        method.addBodyLine("criteria.add(new Criterion(condition, value1, value2));");
        if (criteriaLists.size() > 1) {
            method.addBodyLine("allCriteria = null;");
        }

        answer.addMethod(method);
        FullyQualifiedJavaType listOfDates = new FullyQualifiedJavaType("java.util.List<java.util.Date>");
        if (introspectedTable.hasJDBCDateColumns()) {
            topLevelClass.addImportedType(FullyQualifiedJavaType.getDateInstance());
            topLevelClass.addImportedType(FullyQualifiedJavaType.getNewIteratorInstance());
            method = new Method("addCriterionForJDBCDate");
            method.setVisibility(JavaVisibility.PROTECTED);
            method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition"));
            method.addParameter(new Parameter(FullyQualifiedJavaType.getDateInstance(), "value"));
            method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "property"));
            method.addBodyLine("if (value == null) {");
            method.addBodyLine("throw new RuntimeException(\"Value for \" + property + \" cannot be null\");");
            method.addBodyLine("}");
            method.addBodyLine("addCriterion(condition, new java.sql.Date(value.getTime()), property);");
            answer.addMethod(method);
            method = new Method("addCriterionForJDBCDate");
            method.setVisibility(JavaVisibility.PROTECTED);
            method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition"));
            method.addParameter(new Parameter(listOfDates, "values"));
            method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "property"));
            method.addBodyLine("if (values == null || values.size() == 0) {");
            method.addBodyLine("throw new RuntimeException(\"Value list for \" + property + \" cannot be null or empty\");");
            method.addBodyLine("}");
            method.addBodyLine("List<java.sql.Date> dateList = new ArrayList<>();");
            method.addBodyLine("Iterator<Date> iter = values.iterator();");
            method.addBodyLine("while (iter.hasNext()) {");
            method.addBodyLine("dateList.add(new java.sql.Date(iter.next().getTime()));");
            method.addBodyLine("}");
            method.addBodyLine("addCriterion(condition, dateList, property);");
            answer.addMethod(method);
            method = new Method("addCriterionForJDBCDate");
            method.setVisibility(JavaVisibility.PROTECTED);
            method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition"));
            setParameter(method);
            method.addBodyLine("addCriterion(condition, new java.sql.Date(value1.getTime()), new java.sql.Date(value2.getTime()), property);");
            answer.addMethod(method);
        }

        if (introspectedTable.hasJDBCTimeColumns()) {
            topLevelClass.addImportedType(FullyQualifiedJavaType.getDateInstance());
            topLevelClass.addImportedType(FullyQualifiedJavaType.getNewIteratorInstance());
            method = new Method("addCriterionForJDBCTime");
            method.setVisibility(JavaVisibility.PROTECTED);
            method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition"));
            method.addParameter(new Parameter(FullyQualifiedJavaType.getDateInstance(), "value"));
            method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "property"));
            method.addBodyLine("if (value == null) {");
            method.addBodyLine("throw new RuntimeException(\"Value for \" + property + \" cannot be null\");");
            method.addBodyLine("}");
            method.addBodyLine("addCriterion(condition, new java.sql.Time(value.getTime()), property);");
            answer.addMethod(method);
            method = new Method("addCriterionForJDBCTime");
            method.setVisibility(JavaVisibility.PROTECTED);
            method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition"));
            method.addParameter(new Parameter(listOfDates, "values"));
            method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "property"));
            method.addBodyLine("if (values == null || values.size() == 0) {");
            method.addBodyLine("throw new RuntimeException(\"Value list for \" + property + \" cannot be null or empty\");");
            method.addBodyLine("}");
            method.addBodyLine("List<java.sql.Time> timeList = new ArrayList<>();");
            method.addBodyLine("Iterator<Date> iter = values.iterator();");
            method.addBodyLine("while (iter.hasNext()) {");
            method.addBodyLine("timeList.add(new java.sql.Time(iter.next().getTime()));");
            method.addBodyLine("}");
            method.addBodyLine("addCriterion(condition, timeList, property);");
            answer.addMethod(method);
            method = new Method("addCriterionForJDBCTime");
            method.setVisibility(JavaVisibility.PROTECTED);
            method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition"));
            setParameter(method);
            method.addBodyLine("addCriterion(condition, new java.sql.Time(value1.getTime()), new java.sql.Time(value2.getTime()), property);");
            answer.addMethod(method);
        }

        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            topLevelClass.addImportedType(introspectedColumn.getFullyQualifiedJavaType());
            answer.addMethod(this.getSetNullMethod(introspectedColumn));
            answer.addMethod(this.getSetNotNullMethod(introspectedColumn));
            answer.addMethod(this.getSetEqualMethod(introspectedColumn));
            answer.addMethod(this.getSetNotEqualMethod(introspectedColumn));
            answer.addMethod(this.getSetGreaterThanMethod(introspectedColumn));
            answer.addMethod(this.getSetGreaterThenOrEqualMethod(introspectedColumn));
            answer.addMethod(this.getSetLessThanMethod(introspectedColumn));
            answer.addMethod(this.getSetLessThanOrEqualMethod(introspectedColumn));
            if (introspectedColumn.isJdbcCharacterColumn()) {
                answer.addMethod(this.getSetLikeMethod(introspectedColumn));
                answer.addMethod(this.getSetNotLikeMethod(introspectedColumn));
            }

            answer.addMethod(this.getSetInOrNotInMethod(introspectedColumn, true));
            answer.addMethod(this.getSetInOrNotInMethod(introspectedColumn, false));
            answer.addMethod(this.getSetBetweenOrNotBetweenMethod(introspectedColumn, true));
            answer.addMethod(this.getSetBetweenOrNotBetweenMethod(introspectedColumn, false));
        }

        return answer;
    }

    private void setParameter(Method method) {
        method.addParameter(new Parameter(FullyQualifiedJavaType.getDateInstance(), "value1"));
        method.addParameter(new Parameter(FullyQualifiedJavaType.getDateInstance(), "value2"));
        method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "property"));
        method.addBodyLine("if (value1 == null || value2 == null) {");
        method.addBodyLine("throw new RuntimeException(\"Between values for \" + property + \" cannot be null\");");
        method.addBodyLine("}");
    }

    private Method getSetBetweenOrNotBetweenMethod(IntrospectedColumn introspectedColumn, boolean betweenMethod) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.initializeAndMethodName(introspectedColumn));
        if (betweenMethod) {
            sb.append("Between");
        } else {
            sb.append("NotBetween");
        }

        Method method = new Method(sb.toString());
        method.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
        method.addParameter(new Parameter(type, "value1"));
        method.addParameter(new Parameter(type, "value2"));
        method.setReturnType(FullyQualifiedJavaType.getCriteriaInstance());
        sb.setLength(0);
        sb.append(this.initializeAddLine(introspectedColumn));
        if (betweenMethod) {
            sb.append(" between");
        } else {
            sb.append(" not between");
        }

        sb.append("\", ");
        sb.append("value1, value2");
        sb.append(", \"");
        sb.append(introspectedColumn.getJavaProperty());
        sb.append("\");");
        method.addBodyLine(sb.toString());
        method.addBodyLine("return (Criteria) this;");
        return method;
    }

    private Method getSetInOrNotInMethod(IntrospectedColumn introspectedColumn, boolean inMethod) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.initializeAndMethodName(introspectedColumn));
        if (inMethod) {
            sb.append("In");
        } else {
            sb.append("NotIn");
        }

        Method method = new Method(sb.toString());
        method.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType type = FullyQualifiedJavaType.getNewListInstance();
        if (introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
            type.addTypeArgument(introspectedColumn.getFullyQualifiedJavaType().getPrimitiveTypeWrapper());
        } else {
            type.addTypeArgument(introspectedColumn.getFullyQualifiedJavaType());
        }

        method.addParameter(new Parameter(type, "values"));
        method.setReturnType(FullyQualifiedJavaType.getCriteriaInstance());
        sb.setLength(0);
        sb.append(this.initializeAddLine(introspectedColumn));
        if (inMethod) {
            sb.append(" in");
        } else {
            sb.append(" not in");
        }

        sb.append("\", values, \"");
        sb.append(introspectedColumn.getJavaProperty());
        sb.append("\");");
        method.addBodyLine(sb.toString());
        method.addBodyLine("return (Criteria) this;");
        return method;
    }

    private Method getSetNotLikeMethod(IntrospectedColumn introspectedColumn) {
        return this.getSingleValueMethod(introspectedColumn, "NotLike", "not like");
    }

    private Method getSetLikeMethod(IntrospectedColumn introspectedColumn) {
        return this.getSingleValueMethod(introspectedColumn, "Like", "like");
    }

    private Method getSetLessThanOrEqualMethod(IntrospectedColumn introspectedColumn) {
        return this.getSingleValueMethod(introspectedColumn, "LessThanOrEqualTo", "<=");
    }

    private Method getSetLessThanMethod(IntrospectedColumn introspectedColumn) {
        return this.getSingleValueMethod(introspectedColumn, "LessThan", "<");
    }

    private Method getSetGreaterThenOrEqualMethod(IntrospectedColumn introspectedColumn) {
        return this.getSingleValueMethod(introspectedColumn, "GreaterThanOrEqualTo", ">=");
    }

    private Method getSetGreaterThanMethod(IntrospectedColumn introspectedColumn) {
        return this.getSingleValueMethod(introspectedColumn, "GreaterThan", ">");
    }

    private Method getSetNotEqualMethod(IntrospectedColumn introspectedColumn) {
        return this.getSingleValueMethod(introspectedColumn, "NotEqualTo", "<>");
    }

    private Method getSetEqualMethod(IntrospectedColumn introspectedColumn) {
        return this.getSingleValueMethod(introspectedColumn, "EqualTo", "=");
    }

    private Method getSingleValueMethod(IntrospectedColumn introspectedColumn, String nameFragment, String operator) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.initializeAndMethodName(introspectedColumn));
        sb.append(nameFragment);
        Method method = new Method(sb.toString());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(), "value"));
        method.setReturnType(FullyQualifiedJavaType.getCriteriaInstance());
        sb.setLength(0);
        sb.append(this.initializeAddLine(introspectedColumn));
        sb.append(' ');
        sb.append(operator);
        sb.append("\", ");
        sb.append("value");
        sb.append(", \"");
        sb.append(introspectedColumn.getJavaProperty());
        sb.append("\");");
        method.addBodyLine(sb.toString());
        method.addBodyLine("return (Criteria) this;");
        return method;
    }

    private String initializeAddLine(IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder();
        if (introspectedColumn.isJDBCDateColumn()) {
            sb.append("addCriterionForJDBCDate(\"");
        } else if (introspectedColumn.isJDBCTimeColumn()) {
            sb.append("addCriterionForJDBCTime(\"");
        } else if (StringUtility.stringHasValue(introspectedColumn.getTypeHandler())) {
            sb.append("add");
            sb.append(introspectedColumn.getJavaProperty());
            sb.setCharAt(3, Character.toUpperCase(sb.charAt(3)));
            sb.append("Criterion(\"");
        } else {
            sb.append("addCriterion(\"");
        }

        sb.append(MyBatis3FormattingUtilities.getAliasedActualColumnName(introspectedColumn));
        return sb.toString();
    }

    private Method getSetNotNullMethod(IntrospectedColumn introspectedColumn) {
        return this.getNoValueMethod(introspectedColumn, "IsNotNull", "is not null");
    }

    private Method getSetNullMethod(IntrospectedColumn introspectedColumn) {
        return this.getNoValueMethod(introspectedColumn, "IsNull", "is null");
    }

    private Method getNoValueMethod(IntrospectedColumn introspectedColumn, String nameFragment, String operator) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.initializeAndMethodName(introspectedColumn));
        sb.append(nameFragment);
        Method method = new Method(sb.toString());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getCriteriaInstance());
        sb.setLength(0);
        sb.append("addCriterion(\"");
        sb.append(MyBatis3FormattingUtilities.getAliasedActualColumnName(introspectedColumn));
        sb.append(' ');
        sb.append(operator);
        sb.append("\");");
        method.addBodyLine(sb.toString());
        method.addBodyLine("return (Criteria) this;");
        return method;
    }

    private String initializeAndMethodName(IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append(introspectedColumn.getJavaProperty());
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        sb.insert(0, "and");
        return sb.toString();
    }

    private String addTypeHandledObjectsAndMethods(IntrospectedColumn introspectedColumn, Method constructor, InnerClass innerClass) {
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        sb.append(introspectedColumn.getJavaProperty());
        sb.append("Criteria");
        String answer = sb.toString();
        Field field = new Field(answer, new FullyQualifiedJavaType("java.util.List<Criterion>"));
        field.setVisibility(JavaVisibility.PROTECTED);
        innerClass.addField(field);
        Method method = new Method(JavaBeansUtil.getGetterMethodName(field.getName(), field.getType()));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(field.getType());
        sb.insert(0, "return ");
        sb.append(';');
        method.addBodyLine(sb.toString());
        innerClass.addMethod(method);
        sb.setLength(0);
        sb.append(field.getName());
        sb.append(" = new ArrayList<>();");
        constructor.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("add");
        sb.append(introspectedColumn.getJavaProperty());
        sb.setCharAt(3, Character.toUpperCase(sb.charAt(3)));
        sb.append("Criterion");
        method = new Method(sb.toString());
        method.setVisibility(JavaVisibility.PROTECTED);
        method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition"));
        method.addParameter(new Parameter(FullyQualifiedJavaType.getObjectInstance(), "value"));
        method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "property"));
        method.addBodyLine("if (value == null) {");
        method.addBodyLine("throw new RuntimeException(\"Value for \" + property + \" cannot be null\");");
        method.addBodyLine("}");
        method.addBodyLine(String.format("%s.add(new Criterion(condition, value, \"%s\"));", field.getName(), introspectedColumn.getTypeHandler()));
        method.addBodyLine("allCriteria = null;");
        innerClass.addMethod(method);
        sb.setLength(0);
        sb.append("add");
        sb.append(introspectedColumn.getJavaProperty());
        sb.setCharAt(3, Character.toUpperCase(sb.charAt(3)));
        sb.append("Criterion");
        method = new Method(sb.toString());
        method.setVisibility(JavaVisibility.PROTECTED);
        method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition"));
        method.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(), "value1"));
        method.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(), "value2"));
        method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "property"));
        if (!introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
            method.addBodyLine("if (value1 == null || value2 == null) {");
            method.addBodyLine("throw new RuntimeException(\"Between values for \" + property + \" cannot be null\");");
            method.addBodyLine("}");
        }

        method.addBodyLine(String.format("%s.add(new Criterion(condition, value1, value2, \"%s\"));", field.getName(), introspectedColumn.getTypeHandler()));
        method.addBodyLine("allCriteria = null;");
        innerClass.addMethod(method);
        return answer;
    }
}
