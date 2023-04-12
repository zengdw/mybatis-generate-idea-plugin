package com.zengdw.mybatis.generate;

import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DasTableKey;
import com.intellij.database.model.DasTypedObject;
import com.intellij.database.model.MultiRef;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.util.containers.JBIterable;
import com.zengdw.mybatis.util.DbToolsUtils;
import com.zengdw.mybatis.vo.PropertyVO;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaReservedWords;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.db.ActualTableName;
import org.mybatis.generator.internal.db.SqlReservedWords;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mybatis.generator.internal.util.StringUtility.*;
import static org.mybatis.generator.internal.util.messages.Messages.getString;


/**
 * @author zengd
 * @version 1.0
 * @date 2023/3/29 15:46
 */
public class GenerateContext extends Context {
    public GenerateContext(ModelType defaultModelType) {
        super(defaultModelType);
    }

    @Override
    public void introspectTables(ProgressCallback callback,
                                 List<String> warnings, Set<String> fullyQualifiedTableNames)
            throws InterruptedException {

        getIntrospectedTables().clear();

        callback.startTask(getString("Progress.0"));

        JavaTypeResolver javaTypeResolver = ObjectFactory
                .createJavaTypeResolver(this, warnings);

        Map<ActualTableName, List<IntrospectedColumn>> columns = new HashMap<>();
        for (DbTable dbTable : PropertyVO.of().getTableList()) {
            getColumns(dbTable, columns);
        }
        TableConfiguration tc = new TableConfiguration(this);
        for (DbTable dbTable : PropertyVO.of().getTableList()) {
            tc.setTableName(dbTable.getName());
        }
        removeIgnoredColumns(tc, columns);
        calculateExtraColumnInformation(tc, columns, javaTypeResolver, warnings);
        applyColumnOverrides(tc, columns);
        calculateIdentityColumns(tc, columns);

        List<IntrospectedTable> tables = calculateIntrospectedTables(tc, columns);
        Iterator<IntrospectedTable> iter = tables.iterator();
        while (iter.hasNext()) {
            IntrospectedTable introspectedTable = iter.next();

            if (!introspectedTable.hasAnyColumns()) {
                String warning = getString(
                        "Warning.1", introspectedTable.getFullyQualifiedTable().toString()); //$NON-NLS-1$
                warnings.add(warning);
                iter.remove();
            } else if (!introspectedTable.hasPrimaryKeyColumns()
                    && !introspectedTable.hasBaseColumns()) {
                String warning = getString(
                        "Warning.18", introspectedTable.getFullyQualifiedTable().toString()); //$NON-NLS-1$
                warnings.add(warning);
                iter.remove();
            } else {
                reportIntrospectionWarnings(introspectedTable, tc,
                        introspectedTable.getFullyQualifiedTable(), warnings);
            }
        }
        getIntrospectedTables().addAll(tables);

        callback.checkCancel();
    }

    private void reportIntrospectionWarnings(
            IntrospectedTable introspectedTable,
            TableConfiguration tableConfiguration, FullyQualifiedTable table, List<String> warnings) {
        // make sure that every column listed in column overrides
        // actually exists in the table
        for (ColumnOverride columnOverride : tableConfiguration
                .getColumnOverrides()) {
            if (introspectedTable.getColumn(columnOverride.getColumnName()).isEmpty()) {
                warnings.add(getString("Warning.3", //$NON-NLS-1$
                        columnOverride.getColumnName(), table.toString()));
            }
        }

        // make sure that every column listed in ignored columns
        // actually exists in the table
        for (String string : tableConfiguration.getIgnoredColumnsInError()) {
            warnings.add(getString("Warning.4", //$NON-NLS-1$
                    string, table.toString()));
        }

        tableConfiguration.getGeneratedKey().ifPresent(generatedKey -> {
            if (introspectedTable.getColumn(generatedKey.getColumn()).isEmpty()) {
                if (generatedKey.isIdentity()) {
                    warnings.add(getString("Warning.5", //$NON-NLS-1$
                            generatedKey.getColumn(), table.toString()));
                } else {
                    warnings.add(getString("Warning.6", //$NON-NLS-1$
                            generatedKey.getColumn(), table.toString()));
                }
            }
        });

        for (IntrospectedColumn ic : introspectedTable.getAllColumns()) {
            if (JavaReservedWords.containsWord(ic.getJavaProperty())) {
                warnings.add(getString("Warning.26", //$NON-NLS-1$
                        ic.getActualColumnName(), table.toString()));
            }
        }
    }

    private List<IntrospectedTable> calculateIntrospectedTables(
            TableConfiguration tc,
            Map<ActualTableName, List<IntrospectedColumn>> columns) {
        boolean delimitIdentifiers = tc.isDelimitIdentifiers()
                || stringContainsSpace(tc.getCatalog())
                || stringContainsSpace(tc.getSchema())
                || stringContainsSpace(tc.getTableName());

        List<IntrospectedTable> answer = new ArrayList<>();

        for (Map.Entry<ActualTableName, List<IntrospectedColumn>> entry : columns
                .entrySet()) {
            ActualTableName atn = entry.getKey();
            FullyQualifiedTable table = new FullyQualifiedTable(
                    stringHasValue(tc.getCatalog()) ? atn.getCatalog() : null,
                    stringHasValue(tc.getSchema()) ? atn.getSchema() : null,
                    atn.getTableName(),
                    tc.getDomainObjectName(),
                    tc.getAlias(),
                    isTrue(tc.getProperty(PropertyRegistry.TABLE_IGNORE_QUALIFIERS_AT_RUNTIME)),
                    tc.getProperty(PropertyRegistry.TABLE_RUNTIME_CATALOG),
                    tc.getProperty(PropertyRegistry.TABLE_RUNTIME_SCHEMA),
                    tc.getProperty(PropertyRegistry.TABLE_RUNTIME_TABLE_NAME),
                    delimitIdentifiers,
                    tc.getDomainObjectRenamingRule(),
                    this);

            IntrospectedTable introspectedTable = ObjectFactory
                    .createIntrospectedTable(tc, table, this);

            for (IntrospectedColumn introspectedColumn : entry.getValue()) {
                introspectedTable.addColumn(introspectedColumn);
            }
            calculatePrimaryKey(atn, introspectedTable);

            answer.add(introspectedTable);
        }

        return answer;
    }

    private static void calculatePrimaryKey(ActualTableName atn, IntrospectedTable introspectedTable) {
        for (DbTable dbTable : PropertyVO.of().getTableList()) {
            if (dbTable.getName().equals(atn.getTableName())) {
                DasTableKey primaryKey = DasUtil.getPrimaryKey(dbTable);
                if (primaryKey != null) {
                    MultiRef<? extends DasTypedObject> columnsRef = primaryKey.getColumnsRef();
                    MultiRef.It<? extends DasTypedObject> iterate = columnsRef.iterate();
                    while (iterate.hasNext()) {
                        String columnName = iterate.next();
                        introspectedTable.addPrimaryKeyColumn(columnName);
                    }
                }

                introspectedTable.setTableType(dbTable.getTypeName());
                introspectedTable.setRemarks(dbTable.getComment());
                break;
            }
        }
    }


    private void calculateIdentityColumns(TableConfiguration tc,
                                          Map<ActualTableName, List<IntrospectedColumn>> columns) {
        tc.getGeneratedKey().ifPresent(gk -> {
            for (Map.Entry<ActualTableName, List<IntrospectedColumn>> entry : columns.entrySet()) {
                for (IntrospectedColumn introspectedColumn : entry.getValue()) {
                    if (isMatchedColumn(introspectedColumn, gk)) {
                        if (gk.isIdentity() || gk.isJdbcStandard()) {
                            introspectedColumn.setIdentity(true);
                            introspectedColumn.setSequenceColumn(false);
                        } else {
                            introspectedColumn.setIdentity(false);
                            introspectedColumn.setSequenceColumn(true);
                        }
                    }
                }
            }
        });
    }

    private boolean isMatchedColumn(IntrospectedColumn introspectedColumn, GeneratedKey gk) {
        if (introspectedColumn.isColumnNameDelimited()) {
            return introspectedColumn.getActualColumnName().equals(gk.getColumn());
        } else {
            return introspectedColumn.getActualColumnName().equalsIgnoreCase(gk.getColumn());
        }
    }

    private void applyColumnOverrides(TableConfiguration tc,
                                      Map<ActualTableName, List<IntrospectedColumn>> columns) {
        for (Map.Entry<ActualTableName, List<IntrospectedColumn>> entry : columns
                .entrySet()) {
            for (IntrospectedColumn introspectedColumn : entry.getValue()) {
                ColumnOverride columnOverride = tc
                        .getColumnOverride(introspectedColumn
                                .getActualColumnName());

                if (columnOverride != null) {
                    if (stringHasValue(columnOverride
                            .getJavaProperty())) {
                        introspectedColumn.setJavaProperty(columnOverride
                                .getJavaProperty());
                    }

                    if (stringHasValue(columnOverride
                            .getJavaType())) {
                        introspectedColumn
                                .setFullyQualifiedJavaType(new FullyQualifiedJavaType(
                                        columnOverride.getJavaType()));
                    }

                    if (stringHasValue(columnOverride
                            .getJdbcType())) {
                        introspectedColumn.setJdbcTypeName(columnOverride
                                .getJdbcType());
                    }

                    if (stringHasValue(columnOverride
                            .getTypeHandler())) {
                        introspectedColumn.setTypeHandler(columnOverride
                                .getTypeHandler());
                    }

                    if (columnOverride.isColumnNameDelimited()) {
                        introspectedColumn.setColumnNameDelimited(true);
                    }

                    introspectedColumn.setGeneratedAlways(columnOverride.isGeneratedAlways());

                    introspectedColumn.setProperties(columnOverride
                            .getProperties());

                }
            }
        }
    }

    private void getColumns(DbTable dbTable, Map<ActualTableName, List<IntrospectedColumn>> answer) {
        JBIterable<? extends DasColumn> tableColumns = DasUtil.getColumns(dbTable);
        for (DasColumn column : tableColumns) {
            IntrospectedColumn introspectedColumn = ObjectFactory
                    .createIntrospectedColumn(this);
//            introspectedColumn.setTableAlias(tc.getAlias());
            introspectedColumn.setJdbcType(DbToolsUtils.convertTypeNameToJdbcType(column.getDataType().typeName, DbToolsUtils.extractDatabaseTypeFromUrl(dbTable)));
            introspectedColumn.setActualTypeName(column.getDataType().typeName);
            introspectedColumn.setLength(column.getDataType().getLength());
            introspectedColumn.setActualColumnName(column.getName());
            introspectedColumn.setNullable(!column.isNotNull());
            introspectedColumn.setScale(column.getDataType().getScale());
            introspectedColumn.setRemarks(column.getComment());
            introspectedColumn.setDefaultValue(column.getDefault());
            if (DasUtil.isAutoGenerated(column)) {
                introspectedColumn.setGeneratedColumn(true);
            }
            if (DasUtil.isAutoGenerated(column)) {
                introspectedColumn.setAutoIncrement(true);
            }

            ActualTableName atn = new ActualTableName(null, null, dbTable.getName());
            List<IntrospectedColumn> columns = answer.computeIfAbsent(atn, k -> new ArrayList<>());
            columns.add(introspectedColumn);
        }
    }

    private void removeIgnoredColumns(TableConfiguration tc,
                                      Map<ActualTableName, List<IntrospectedColumn>> columns) {
        for (Map.Entry<ActualTableName, List<IntrospectedColumn>> entry : columns
                .entrySet()) {
            entry.getValue().removeIf(introspectedColumn -> tc
                    .isColumnIgnored(introspectedColumn
                            .getActualColumnName()));
        }
    }

    private void calculateExtraColumnInformation(TableConfiguration tc,
                                                 Map<ActualTableName, List<IntrospectedColumn>> columns,
                                                 JavaTypeResolver javaTypeResolver,
                                                 List<String> warnings) {
        StringBuilder sb = new StringBuilder();
        Pattern pattern = null;
        String replaceString = null;
        if (tc.getColumnRenamingRule() != null) {
            pattern = Pattern.compile(tc.getColumnRenamingRule()
                    .getSearchString());
            replaceString = tc.getColumnRenamingRule().getReplaceString();
            replaceString = replaceString == null ? "" : replaceString; //$NON-NLS-1$
        }

        for (Map.Entry<ActualTableName, List<IntrospectedColumn>> entry : columns
                .entrySet()) {
            for (IntrospectedColumn introspectedColumn : entry.getValue()) {
                String calculatedColumnName;
                if (pattern == null) {
                    calculatedColumnName = introspectedColumn
                            .getActualColumnName();
                } else {
                    Matcher matcher = pattern.matcher(introspectedColumn
                            .getActualColumnName());
                    calculatedColumnName = matcher.replaceAll(replaceString);
                }

                if (isTrue(tc
                        .getProperty(PropertyRegistry.TABLE_USE_ACTUAL_COLUMN_NAMES))) {
                    introspectedColumn.setJavaProperty(
                            JavaBeansUtil.getValidPropertyName(calculatedColumnName));
                } else if (isTrue(tc
                        .getProperty(PropertyRegistry.TABLE_USE_COMPOUND_PROPERTY_NAMES))) {
                    sb.setLength(0);
                    sb.append(calculatedColumnName);
                    sb.append('_');
                    sb.append(JavaBeansUtil.getCamelCaseString(
                            introspectedColumn.getRemarks(), true));
                    introspectedColumn.setJavaProperty(
                            JavaBeansUtil.getValidPropertyName(sb.toString()));
                } else {
                    introspectedColumn.setJavaProperty(
                            JavaBeansUtil.getCamelCaseString(calculatedColumnName, false));
                }

                FullyQualifiedJavaType fullyQualifiedJavaType = javaTypeResolver
                        .calculateJavaType(introspectedColumn);

                if (fullyQualifiedJavaType != null) {
                    introspectedColumn
                            .setFullyQualifiedJavaType(fullyQualifiedJavaType);
                    introspectedColumn.setJdbcTypeName(javaTypeResolver
                            .calculateJdbcTypeName(introspectedColumn));
                } else {
                    // type cannot be resolved. Check for ignored or overridden
                    boolean warn = !tc.isColumnIgnored(introspectedColumn.getActualColumnName());

                    ColumnOverride co = tc.getColumnOverride(introspectedColumn
                            .getActualColumnName());
                    if (co != null
                            && stringHasValue(co.getJavaType())) {
                        warn = false;
                    }

                    // if the type is not supported, then we'll report a warning
                    if (warn) {
                        introspectedColumn
                                .setFullyQualifiedJavaType(FullyQualifiedJavaType
                                        .getObjectInstance());
                        introspectedColumn.setJdbcTypeName("OTHER"); //$NON-NLS-1$

                        String warning = getString("Warning.14", //$NON-NLS-1$
                                Integer.toString(introspectedColumn.getJdbcType()),
                                entry.getKey().toString(),
                                introspectedColumn.getActualColumnName());

                        warnings.add(warning);
                    }
                }

                if (autoDelimitKeywords()
                        && SqlReservedWords.containsWord(introspectedColumn
                        .getActualColumnName())) {
                    introspectedColumn.setColumnNameDelimited(true);
                }

                if (tc.isAllColumnDelimitingEnabled()) {
                    introspectedColumn.setColumnNameDelimited(true);
                }
            }
        }
    }

}
