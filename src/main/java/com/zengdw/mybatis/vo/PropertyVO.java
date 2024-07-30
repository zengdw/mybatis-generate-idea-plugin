package com.zengdw.mybatis.vo;

import com.intellij.database.psi.DbTable;

import java.util.List;

/**
 * @author zengd
 */
public class PropertyVO {
    private MyModule module;
    private String javaModelPath = "src/main/java";
    private String mapperPath = "src/main/java";
    private String mapperXmlPath = "src/main/java";
    private String javaModelPackage;
    private String mapperPackage;
    private String mapperXmlPackage;
    private Boolean mapperAnnotation = false;
    private Boolean comment = true;
    private Boolean java8Date = true;
    private Boolean example = false;
    private Boolean lombok = false;
    private Boolean serializable = false;
    private Boolean mergeFile = true;
    private List<DbTable> tableList;
    private String dbType;
    private Boolean blob = false;
    private Boolean isInt = false;
    private static PropertyVO instance;

    public static PropertyVO of() {
        if (null == instance) {
            instance = new PropertyVO();
        }
        return instance;
    }

    private PropertyVO() {
    }

    public MyModule getModule() {
        return module;
    }

    public void setModule(MyModule module) {
        this.module = module;
    }

    public String getJavaModelPath() {
        return javaModelPath;
    }

    public void setJavaModelPath(final String javaModelPath) {
        this.javaModelPath = javaModelPath;
    }

    public String getMapperPath() {
        return mapperPath;
    }

    public void setMapperPath(final String mapperPath) {
        this.mapperPath = mapperPath;
    }

    public String getMapperXmlPath() {
        return mapperXmlPath;
    }

    public void setMapperXmlPath(final String mapperXmlPath) {
        this.mapperXmlPath = mapperXmlPath;
    }

    public String getJavaModelPackage() {
        return javaModelPackage;
    }

    public void setJavaModelPackage(final String javaModelPackage) {
        this.javaModelPackage = javaModelPackage;
    }

    public String getMapperPackage() {
        return mapperPackage;
    }

    public void setMapperPackage(final String mapperPackage) {
        this.mapperPackage = mapperPackage;
    }

    public String getMapperXmlPackage() {
        return mapperXmlPackage;
    }

    public void setMapperXmlPackage(final String mapperXmlPackage) {
        this.mapperXmlPackage = mapperXmlPackage;
    }

    public boolean isMapperAnnotation() {
        return mapperAnnotation;
    }

    public void setMapperAnnotation(final boolean mapperAnnotation) {
        this.mapperAnnotation = mapperAnnotation;
    }

    public boolean isComment() {
        return comment;
    }

    public void setComment(final boolean comment) {
        this.comment = comment;
    }

    public boolean getJava8Date() {
        return java8Date;
    }

    public void setJava8Date(final boolean java8Date) {
        this.java8Date = java8Date;
    }

    public boolean isExample() {
        return example;
    }

    public void setExample(final boolean example) {
        this.example = example;
    }

    public boolean isLombok() {
        return lombok;
    }

    public void setLombok(final boolean lombok) {
        this.lombok = lombok;
    }

    public boolean isSerializable() {
        return serializable;
    }

    public void setSerializable(final boolean serializable) {
        this.serializable = serializable;
    }

    public Boolean getBlob() {
        return blob;
    }

    public void setBlob(Boolean blob) {
        this.blob = blob;
    }

    public boolean isMergeFile() {
        return mergeFile;
    }

    public void setMergeFile(final boolean mergeFile) {
        this.mergeFile = mergeFile;
    }

    public List<DbTable> getTableList() {
        return tableList;
    }

    public void setTableList(List<DbTable> tableList) {
        this.tableList = tableList;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public Boolean getInt() {
        return isInt;
    }

    public void setInt(Boolean anInt) {
        isInt = anInt;
    }
}