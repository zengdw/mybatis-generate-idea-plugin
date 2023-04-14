package com.zengdw.mybatis.generate;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.zengdw.mybatis.override.XmlFileMergerJaxp;
import com.zengdw.mybatis.vo.PropertyVO;
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
        context.generateFiles(callback, generatedJavaFiles,
                generatedXmlFiles, generatedKotlinFiles, otherGeneratedFiles, warnings);

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

    private void writeGeneratedFile(GeneratedFile gf, ProgressCallback callback)
            throws Exception {
        File targetFile;
        String source;
        File directory = shellCallback.getDirectory(gf
                .getTargetProject(), gf.getTargetPackage());
        targetFile = new File(directory, gf.getFileName());
        if (targetFile.exists()) {
            if (shellCallback.isOverwriteEnabled()) {
                source = gf.getFormattedContent();
            } else {
                source = gf.getFormattedContent();
                targetFile = getUniqueFileName(directory, gf
                        .getFileName());
            }
        } else {
            source = gf.getFormattedContent();
        }

        callback.checkCancel();
        callback.startTask(getString(
                "Progress.15", targetFile.getName())); //$NON-NLS-1$
        writeFile(targetFile, source, gf.getFileEncoding());
    }

    private void writeGeneratedJavaFile(GeneratedJavaFile gjf, ProgressCallback callback)
            throws Exception {
        File targetFile;
        String source;
        File directory = shellCallback.getDirectory(gjf
                .getTargetProject(), gjf.getTargetPackage());
        targetFile = new File(directory, gjf.getFileName());
        if (targetFile.exists()) {
            if (shellCallback.isMergeSupported()) {
                source = shellCallback.mergeJavaFile(gjf
                                .getFormattedContent(), targetFile,
                        MergeConstants.getOldElementTags(),
                        gjf.getFileEncoding());
            } else if (shellCallback.isOverwriteEnabled()) {
                source = gjf.getFormattedContent();
            } else {
                source = gjf.getFormattedContent();
                targetFile = getUniqueFileName(directory, gjf
                        .getFileName());
            }
        } else {
            source = gjf.getFormattedContent();
        }

        callback.checkCancel();
        callback.startTask(getString(
                "Progress.15", targetFile.getName())); //$NON-NLS-1$
        writeFile(targetFile, source, gjf.getFileEncoding());
    }

    private void writeGeneratedXmlFile(GeneratedXmlFile gxf, ProgressCallback callback)
            throws Exception {
        File targetFile;
        String source;
        File directory = shellCallback.getDirectory(gxf
                .getTargetProject(), gxf.getTargetPackage());
        targetFile = new File(directory, gxf.getFileName());
        if (targetFile.exists()) {
            if (gxf.isMergeable()) {
                source = XmlFileMergerJaxp.getMergedSource(gxf,
                        targetFile);
            } else if (shellCallback.isOverwriteEnabled()) {
                source = gxf.getFormattedContent();
            } else {
                source = gxf.getFormattedContent();
                targetFile = getUniqueFileName(directory, gxf
                        .getFileName());
            }
        } else {
            source = gxf.getFormattedContent();
        }

        callback.checkCancel();
        callback.startTask(getString(
                "Progress.15", targetFile.getName())); //$NON-NLS-1$
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
            throw new RuntimeException(getString(
                    "RuntimeError.3", directory.getAbsolutePath())); //$NON-NLS-1$
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
        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        pluginConfiguration.setConfigurationType("com.zengdw.mybatis.plugins.EmptyStrPlugin");
        context.addPluginConfiguration(pluginConfiguration);

        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        commentGeneratorConfiguration.setConfigurationType("com.zengdw.mybatis.generate.CommentGenerator");
        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);

        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        javaTypeResolverConfiguration.addProperty("useJSR310Types", "true");
        javaTypeResolverConfiguration.addProperty("forceBigDecimals", "false");
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
