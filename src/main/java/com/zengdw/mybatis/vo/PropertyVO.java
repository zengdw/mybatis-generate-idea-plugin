package com.zengdw.mybatis.vo;

import com.intellij.openapi.module.Module;

import java.util.List;

/**
 * @author zengd
 * @date 2023/3/18 21:45
 */
public class PropertyVO {
    private List<Module> moduleList;
    private final String javaModelPath = "src/main/java";
    private String javaModelPackage;
    private final String mapperPath = "src/main/java";
    private String mapperPackage;
    private final String mapperXmlPath = "src/main/java";
    private String mapperXmlPackage;
}
