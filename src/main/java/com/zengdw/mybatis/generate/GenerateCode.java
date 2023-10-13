package com.zengdw.mybatis.generate;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.zengdw.mybatis.override.XmlFileMergerJaxp;
import com.zengdw.mybatis.ui.PropertyUI;
import com.zengdw.mybatis.vo.PropertyVO;
import org.jetbrains.annotations.NotNull;
import org.mybatis.generator.api.*;
import org.mybatis.generator.codegen.RootClassInfo;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.ObjectFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * @author zengd
 * @version 1.0
 * @date 2023/3/29 11:13
 */
public class GenerateCode {
    private final ShellCallback shellCallback = new MyShellCallback();
    private final List<GeneratedJavaFile> generatedJavaFiles = new ArrayList<>();
    private final List<GeneratedXmlFile> generatedXmlFiles = new ArrayList<>();
    private final List<GeneratedKotlinFile> generatedKotlinFiles = new ArrayList<>();
    private final List<GeneratedFile> otherGeneratedFiles = new ArrayList<>();

    public void generate() throws Exception {
        List<String> warnings = new ArrayList<>();
        ProgressCallback callback = new ProgressCallback() {
        };

        generatedJavaFiles.clear();
        generatedXmlFiles.clear();
        ObjectFactory.reset();
        RootClassInfo.reset();

        GenerateContext context = generateContext();
        context.introspectTables(callback, warnings, null);
        context.generateFiles(callback, generatedJavaFiles, generatedXmlFiles, generatedKotlinFiles, otherGeneratedFiles, warnings);

        for (GeneratedXmlFile gxf : generatedXmlFiles) {
            writeGeneratedXmlFile(gxf, callback);
        }

        for (GeneratedJavaFile gjf : generatedJavaFiles) {
            writeGeneratedJavaFile(gjf, callback);
        }

        for (GeneratedKotlinFile gkf : generatedKotlinFiles) {
            writeGeneratedFile(gkf, callback);
        }

        for (GeneratedFile gf : otherGeneratedFiles) {
            writeGeneratedFile(gf, callback);
        }
    }

    private void writeGeneratedFile(GeneratedFile gf, ProgressCallback callback) throws Exception {
        String source;
        File directory = shellCallback.getDirectory(gf.getTargetProject(), gf.getTargetPackage());
        File targetFile = new File(directory, gf.getFileName());
        if (targetFile.exists()) {
            if (shellCallback.isOverwriteEnabled()) {
                source = gf.getFormattedContent();
            } else {
                source = gf.getFormattedContent();
                targetFile = getUniqueFileName(directory, gf.getFileName());
            }
        } else {
            source = gf.getFormattedContent();
        }

        callback.checkCancel();
        callback.startTask(getString("Progress.15", targetFile.getName())); //$NON-NLS-1$
        writeFile(targetFile, source, gf.getFileEncoding());
    }

    private void writeGeneratedJavaFile(GeneratedJavaFile gjf, ProgressCallback callback) throws Exception {
        String source;
        File directory = shellCallback.getDirectory(gjf.getTargetProject(), gjf.getTargetPackage());
        File targetFile = new File(directory, gjf.getFileName());
        if (targetFile.exists()) {
            if (shellCallback.isMergeSupported()) {
                source = shellCallback.mergeJavaFile(gjf.getFormattedContent(), targetFile, MergeConstants.getOldElementTags(), gjf.getFileEncoding());
            } else if (shellCallback.isOverwriteEnabled()) {
                source = gjf.getFormattedContent();
            } else {
                source = gjf.getFormattedContent();
                targetFile = getUniqueFileName(directory, gjf.getFileName());
            }
        } else {
            source = gjf.getFormattedContent();
        }

        callback.checkCancel();
        callback.startTask(getString("Progress.15", targetFile.getName())); //$NON-NLS-1$
        writeFile(targetFile, source, gjf.getFileEncoding());
    }

    private void writeGeneratedXmlFile(GeneratedXmlFile gxf, ProgressCallback callback) throws Exception {
        String source;
        File directory = shellCallback.getDirectory(gxf.getTargetProject(), gxf.getTargetPackage());
        File targetFile = new File(directory, gxf.getFileName());
        if (targetFile.exists()) {
            if (gxf.isMergeable()) {
                source = XmlFileMergerJaxp.getMergedSource(gxf, targetFile);
            } else if (shellCallback.isOverwriteEnabled()) {
                source = gxf.getFormattedContent();
            } else {
                source = gxf.getFormattedContent();
                targetFile = getUniqueFileName(directory, gxf.getFileName());
            }
        } else {
            source = gxf.getFormattedContent();
        }

        callback.checkCancel();
        callback.startTask(getString("Progress.15", targetFile.getName())); //$NON-NLS-1$
        writeFile(targetFile, source, gxf.getFileEncoding());
    }

    private void writeFile(File file, String content, String fileEncoding) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file, false)) {
            OutputStreamWriter osw;
            if (fileEncoding == null) {
                osw = new OutputStreamWriter(fos);
            } else {
                osw = new OutputStreamWriter(fos, Charset.forName(fileEncoding));
            }

            try (BufferedWriter bw = new BufferedWriter(osw)) {
                bw.write(content);
            }
        }
    }

    private File getUniqueFileName(File directory, String fileName) {
        File answer = null;

        // try up to 1000 times to generate a unique file name
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < 1000; i++) {
            sb.setLength(0);
            sb.append(fileName);
            sb.append('.');
            sb.append(i);

            File testFile = new File(directory, sb.toString());
            if (!testFile.exists()) {
                answer = testFile;
                break;
            }
        }

        if (answer == null) {
            throw new RuntimeException(getString("RuntimeError.3", directory.getAbsolutePath())); //$NON-NLS-1$
        }

        return answer;
    }

    private static GenerateContext generateContext() {
        PropertyVO property = PropertyVO.of();
        GenerateContext context = new GenerateContext(null);

        context.setId("simple");
        context.setTargetRuntime(property.isExample() ? "MyBatis3" : "MyBatis3Simple");

        context.addProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING, "UTF-8");
        context.addProperty(PropertyRegistry.CONTEXT_AUTO_DELIMIT_KEYWORDS, "true");
        if ("MySql".equals(property.getDbType())) {
            context.addProperty(PropertyRegistry.CONTEXT_BEGINNING_DELIMITER, "`");
            context.addProperty(PropertyRegistry.CONTEXT_ENDING_DELIMITER, "`");
        } else if ("Oracle".equals(property.getDbType())) {
            context.addProperty(PropertyRegistry.CONTEXT_BEGINNING_DELIMITER, "\"");
            context.addProperty(PropertyRegistry.CONTEXT_ENDING_DELIMITER, "\"");
        }

        if (property.isLombok()) {
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("com.zengdw.mybatis.plugins.LombokPlugin");
            context.addPluginConfiguration(pluginConfiguration);
        }
        if (!property.getBlob()) {
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("com.zengdw.mybatis.plugins.NoBlobFieldPlugin");
            context.addPluginConfiguration(pluginConfiguration);
        }
        if (property.isSerializable()) {
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.mybatis.generator.plugins.SerializablePlugin");
            pluginConfiguration.addProperty("suppressJavaInterface", "false");
            context.addPluginConfiguration(pluginConfiguration);
        }
        PluginConfiguration mapperSelectivePluginConfiguration = new PluginConfiguration();
        mapperSelectivePluginConfiguration.setConfigurationType("com.zengdw.mybatis.plugins.MapperSelectivePlugin");
        context.addPluginConfiguration(mapperSelectivePluginConfiguration);

        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        pluginConfiguration.setConfigurationType("com.zengdw.mybatis.plugins.EmptyStrPlugin");
        context.addPluginConfiguration(pluginConfiguration);

        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        commentGeneratorConfiguration.setConfigurationType("com.zengdw.mybatis.generate.CommentGenerator");
        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);

        JavaTypeResolverConfiguration javaTypeResolverConfiguration = getJavaTypeResolverConfiguration();
        context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);

        VirtualFile virtualFile = ProjectUtil.guessModuleDir(property.getModule().getModule());
        String modulePath = virtualFile.getPresentableUrl().replace(File.separator, "/");

        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        javaModelGeneratorConfiguration.setTargetPackage(property.getJavaModelPackage());
        javaModelGeneratorConfiguration.setTargetProject(PropertyUI.disposeModulePath(modulePath, property.getJavaModelPath()) + property.getJavaModelPath());
        // 从数据库返回的值被清理前后的空格
        javaModelGeneratorConfiguration.addProperty("trimStrings", "true");
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetPackage(property.getMapperXmlPackage());
        sqlMapGeneratorConfiguration.setTargetProject(PropertyUI.disposeModulePath(modulePath, property.getMapperXmlPath()) + property.getMapperXmlPath());
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        javaClientGeneratorConfiguration.setTargetPackage(property.getMapperPackage());
        javaClientGeneratorConfiguration.setTargetProject(PropertyUI.disposeModulePath(modulePath, property.getMapperPath()) + property.getMapperPath());
        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

        TableConfiguration tc = new TableConfiguration(context);
        for (DbTable dbTable : property.getTableList()) {
            tc.setTableName(dbTable.getName());
        }
        context.addTableConfiguration(tc);
        return context;
    }

    @NotNull
    private static JavaTypeResolverConfiguration getJavaTypeResolverConfiguration() {
        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        /*
            useJSR310Types:
            true	JDBC Type	Resolved Java Type
                    DATE	java.time.LocalDate
                    TIME	java.time.LocalTime
                    TIMESTAMP	java.time.LocalDateTime
            false	使用java8时间类型
         */
        boolean java8Date = PropertyVO.of().getJava8Date();
        javaTypeResolverConfiguration.addProperty("useJSR310Types", String.valueOf(java8Date));
        /*
            forceBigDecimals:
            false	这是默认值
                    当属性为 false 或未指定时，缺省 Java 类型解析器将尝试通过替换整型类型（如果可能）使 JDBC 十进制和数字类型更易于使用。替换规则如下：
                    如果小数位数大于零，或者长度大于 18，则将使用 java.math.BigDecimal 类型。
                    如果小数位数为零，并且长度为 10 到 18，则 Java 类型解析器将替换 java.lang.Long。
                    如果小数位数为零，并且长度为 5 到 9，则 Java 类型解析程序将替换 java.lang.Integer。
                    如果小数位数为零，并且长度小于 5，则 Java 类型解析器将替换 java.lang.Short。
            true	当该属性为 true 时，如果数据库列的类型为 DECIMAL 或 NUMERIC，则 Java 类型解析程序将始终使用 java.math.BigDecimal 。
         */
        javaTypeResolverConfiguration.addProperty("forceBigDecimals", "false");
        return javaTypeResolverConfiguration;
    }
}
