package com.zengdw.mybatis.util;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.util.text.StringUtil;

import java.sql.Types;

/**
 * The type Db tools utils.
 *
 * @author zengd
 */
public class DbToolsUtils {

    public static String extractDatabaseTypeFromUrl(DbTable currentTable) {
        String url = currentTable.getDataSource().getConnectionConfig().getUrl();
        return extractDatabaseTypeFromUrl(url);
    }

    /**
     * Extract database type from url string.
     *
     * @param url the url
     * @return the string
     */
    public static String extractDatabaseTypeFromUrl(String url) {
        if (url == null) {
            return "";
        } else {
            url = url.toLowerCase();
            if (url.contains(":mysql")) {
                return "MySql";
            } else if (url.contains(":oracle")) {
                return "Oracle";
            } else if (url.contains(":postgresql")) {
                return "PostgreSQL";
            } else if (url.contains(":sqlserver")) {
                return "SqlServer";
            } else {
                return url.contains(":sqlite") ? "Sqlite" : "";
            }
        }
    }

    /**
     * Convert type name to jdbc type int.
     *
     * @param jdbcTypeName the jdbc type name
     * @param databaseType the database type
     * @return the int
     */
    public static int convertTypeNameToJdbcType(String jdbcTypeName, String databaseType) {
        if (StringUtil.isEmpty(jdbcTypeName)) {
            return Types.OTHER;
        }

        String fixed = jdbcTypeName.toUpperCase();
        if (fixed.contains("BIGINT")) {
            return Types.BIGINT;
        } else if (fixed.contains("TINYINT")) {
            return Types.TINYINT;
        } else if (fixed.contains("LONGVARBINARY")) {
            return Types.LONGVARBINARY;
        } else if (fixed.contains("VARBINARY")) {
            return Types.VARBINARY;
        } else if (fixed.contains("LONGVARCHAR")) {
            return Types.LONGVARCHAR;
        } else if (fixed.contains("SMALLINT")) {
            return Types.SMALLINT;
        } else if (fixed.contains("DATETIME")) {
            return Types.TIMESTAMP;
        } else if ("DATE".equals(fixed) && "Oracle".equals(databaseType)) {
            return Types.TIMESTAMP;
        } else if (fixed.contains("NUMBER")) {
            return Types.DECIMAL;
        } else if (fixed.contains("BOOLEAN")) {
            return Types.BOOLEAN;
        } else if (fixed.contains("BINARY")) {
            return Types.VARBINARY;
        } else if (fixed.contains("BIT")) {
            return Types.BIT;
        } else if (fixed.contains("BOOL")) {
            return Types.BOOLEAN;
        } else if (fixed.contains("DATE")) {
            return Types.DATE;
        } else if (fixed.contains("TIMESTAMP")) {
            return Types.TIMESTAMP;
        } else if (fixed.contains("TIME")) {
            return Types.TIME;
        } else if (fixed.contains("TEXT") && "MySql".equals(databaseType)) {
            return -1;
        } else if (!fixed.contains("REAL") && !fixed.contains("NUMBER")) {
            if (fixed.contains("FLOAT")) {
                return Types.FLOAT;
            } else if (fixed.contains("DOUBLE")) {
                return Types.DOUBLE;
            } else if ("CHAR".equals(fixed)) {
                return Types.CHAR;
            } else if (fixed.contains("INT")) {
                return Types.INTEGER;
            } else if (fixed.contains("DECIMAL")) {
                return Types.DECIMAL;
            } else if (fixed.contains("NUMERIC")) {
                return Types.NUMERIC;
            } else if (!fixed.contains("CHAR")) {
                if (fixed.contains("BLOB")) {
                    return Types.BLOB;
                } else if (fixed.contains("CLOB")) {
                    return Types.CLOB;
                } else {
                    return fixed.contains("REFERENCE") ? Types.REF : Types.OTHER;
                }
            } else {
                return Types.VARCHAR;
            }
        } else {
            return Types.REAL;
        }

    }

}
