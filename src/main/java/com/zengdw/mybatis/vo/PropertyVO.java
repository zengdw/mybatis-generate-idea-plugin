package com.zengdw.mybatis.vo;

import com.intellij.openapi.module.Module;
import lombok.Data;

import java.util.List;

/**
 * @author zengd
 * @date 2023/3/18 21:45
 */
@Data
public class PropertyVO {
    private List<Module> moduleList;
    private String javaModelPath = "src/main/java";
    private String javaModelPackage;
    private String mapperPath = "src/main/java";
    private String mapperPackage;
    private String mapperXmlPath = "src/main/java";
    private String mapperXmlPackage;
}
