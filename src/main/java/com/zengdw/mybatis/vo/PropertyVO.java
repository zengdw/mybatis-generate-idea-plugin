package com.zengdw.mybatis.vo;

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
    private boolean mapperAnnotation;
    private boolean comment;
    private boolean mybatisPlus;
    private boolean example;
    private boolean lombok;
    private boolean serializable;
    private boolean trimString;
    private boolean toString;
    private boolean mergeFile;

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

    public boolean isMybatisPlus() {
        return mybatisPlus;
    }

    public void setMybatisPlus(final boolean mybatisPlus) {
        this.mybatisPlus = mybatisPlus;
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

    public boolean isTrimString() {
        return trimString;
    }

    public void setTrimString(final boolean trimString) {
        this.trimString = trimString;
    }

    public boolean isToString() {
        return toString;
    }

    public void setToString(final boolean toString) {
        this.toString = toString;
    }

    public boolean isMergeFile() {
        return mergeFile;
    }

    public void setMergeFile(final boolean mergeFile) {
        this.mergeFile = mergeFile;
    }
}