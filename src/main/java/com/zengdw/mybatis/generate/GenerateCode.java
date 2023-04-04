package com.zengdw.mybatis.generate;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.zengdw.mybatis.vo.PropertyVO;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zengd
 * @version 1.0
 * @date 2023/3/29 11:13
 */
public class GenerateCode {
    public static void generate() throws Exception {
        List<String> warnings = new ArrayList<>();
        ShellCallback callback = new MyShellCallback();

        GenerateContext context = generateContext();
        /*context.setIntrospectedTables(PropertyVO.of().getTableList());

        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(new ProgressCallback() {
            @Override
            public void done() {
                Messages.showMessageDialog("代码生成完成", "Tips", null);
            }
        });*/
    }

    private static GenerateContext generateContext() {
        PropertyVO property = PropertyVO.of();
        GenerateContext context = new GenerateContext(null);

        context.setId("simple");
        context.setTargetRuntime(property.isExample() ? "MyBatis3" : "MyBatis3Simple");

        if (property.isLombok()) {
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("com.zengdw.mybatis.plugins.LombokPlugin");
            context.addPluginConfiguration(pluginConfiguration);
        }
        if (property.isSerializable()) {
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.mybatis.generator.plugins.SerializablePlugin");
            pluginConfiguration.addProperty("suppressJavaInterface", "false");
            context.addPluginConfiguration(pluginConfiguration);
        }
        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        pluginConfiguration.setConfigurationType("com.zengdw.mybatis.plugins.EmptyStrPlugin");
        context.addPluginConfiguration(pluginConfiguration);

        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        commentGeneratorConfiguration.setConfigurationType("com.zengdw.mybatis.generate.CommentGenerator");
        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);

        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        javaTypeResolverConfiguration.addProperty("useJSR310Types", "true");
        context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);

        VirtualFile virtualFile = ProjectUtil.guessModuleDir(property.getModule().getModule());
        String modulePath = virtualFile.getPresentableUrl().replace(File.separator, "/") + File.separator;

        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        javaModelGeneratorConfiguration.setTargetPackage(property.getJavaModelPackage());
        javaModelGeneratorConfiguration.setTargetProject(modulePath + property.getJavaModelPath());
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetPackage(property.getMapperXmlPackage());
        sqlMapGeneratorConfiguration.setTargetProject(modulePath + property.getMapperXmlPath());
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        javaClientGeneratorConfiguration.setTargetPackage(property.getMapperPackage());
        javaClientGeneratorConfiguration.setTargetProject(modulePath + property.getMapperPath());
        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

        TableConfiguration tc = new TableConfiguration(context);
        for (DbTable dbTable : property.getTableList()) {
            tc.setTableName(dbTable.getName());
        }
        context.addTableConfiguration(tc);
        return context;
    }
}
