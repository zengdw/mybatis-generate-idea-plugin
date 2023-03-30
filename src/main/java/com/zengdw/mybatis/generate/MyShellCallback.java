package com.zengdw.mybatis.generate;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.zengdw.mybatis.vo.PropertyVO;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.exception.ShellException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * 文件合并
 *
 * @author zengd
 * @version 1.0
 * @date 2022/4/7 14:57
 */
public class MyShellCallback implements ShellCallback {
    private final Logger log = LoggerFactory.getLogger(MyShellCallback.class);

    @Override
    public File getDirectory(String targetProject, String targetPackage)
            throws ShellException {
        File project = new File(targetProject);
        if (!project.exists()) {
            if (!project.mkdirs()) {
                throw new ShellException("文件夹" + targetProject + "创建失败");
            }
        }
        if (!project.isDirectory()) {
            throw new ShellException(getString("Warning.9",
                    targetProject));
        }

        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(targetPackage, ".");
        while (st.hasMoreTokens()) {
            sb.append(st.nextToken());
            sb.append(File.separatorChar);
        }

        File directory = new File(project, sb.toString());
        if (!directory.isDirectory()) {
            boolean rc = directory.mkdirs();
            if (!rc) {
                throw new ShellException(getString("Warning.10",
                        directory.getAbsolutePath()));
            }
        }

        return directory;
    }


    @Override
    public String mergeJavaFile(String newFileSource, File existingFile, String[] javadocTags, String fileEncoding) throws ShellException {
        try {
            if (!existingFile.getName().endsWith("Example.java")) {
                log.debug("合并文件【{}】", existingFile.getAbsolutePath());
                return getNewJavaFile(newFileSource, existingFile.getPath());
            }
            return newFileSource;
        } catch (Exception e) {
            throw new ShellException(e);
        }
    }

    @Override
    public boolean isMergeSupported() {
        return true;
    }

    @Override
    public boolean isOverwriteEnabled() {
        return true;
    }

    private String getNewJavaFile(String newFileSource, String existingFileFullPath) throws FileNotFoundException {
        JavaParser javaParser = new JavaParser();
        CompilationUnit newCompilationUnit = javaParser.parse(newFileSource).getResult().orElseThrow();
        CompilationUnit existingCompilationUnit = javaParser.parse(new File(existingFileFullPath)).getResult().orElseThrow();
        return merge(newCompilationUnit, existingCompilationUnit).toString();
    }

    private CompilationUnit merge(CompilationUnit newCompilationUnit, CompilationUnit oldCompilationUnit) {
        //合并imports
        NodeList<ImportDeclaration> imports = newCompilationUnit.getImports();
        imports.addAll(oldCompilationUnit.getImports());
        Set<ImportDeclaration> importSet = new HashSet<>(imports);
        List<String> removeAnnotations = new ArrayList<>();
        if (!PropertyVO.of().isLombok()) {
            importSet.removeIf(next -> {
                String name = next.getNameAsString();
                boolean b = name.startsWith("lombok");
                if (b) {
                    removeAnnotations.add(name.substring(name.lastIndexOf(".") + 1));
                }
                return b;
            });
        }
        newCompilationUnit.setImports(new NodeList<>(importSet));

        NodeList<TypeDeclaration<?>> newTypes = newCompilationUnit.getTypes();
        NodeList<TypeDeclaration<?>> oldTypes = oldCompilationUnit.getTypes();
        if (oldTypes.size() == 0) {
            return newCompilationUnit;
        }

        for (int i = 0; i < newTypes.size(); i++) {
            TypeDeclaration<?> newTypeDeclaration = newTypes.get(i);
            TypeDeclaration<?> oldTypeDeclaration = oldTypes.get(i);

            //合并注解
            NodeList<AnnotationExpr> annotations = newTypeDeclaration.getAnnotations();
            annotations.addAll(oldTypeDeclaration.getAnnotations());
            Set<AnnotationExpr> annotationExprSet = new HashSet<>(annotations);
            if (!PropertyVO.of().isLombok()) {
                annotationExprSet.removeIf(annotationExpr -> removeAnnotations.contains(annotationExpr.getNameAsString()));
            }
            newTypeDeclaration.setAnnotations(new NodeList<>(annotationExprSet));

            //合并父类
            NodeList<ClassOrInterfaceType> extendedTypes = ((ClassOrInterfaceDeclaration) newTypeDeclaration).getExtendedTypes();
            extendedTypes.addAll(((ClassOrInterfaceDeclaration) oldTypeDeclaration).getExtendedTypes());
            Set<ClassOrInterfaceType> extensionTypes = new HashSet<>(extendedTypes);
            ((ClassOrInterfaceDeclaration) newTypeDeclaration).setImplementedTypes(new NodeList<>(extensionTypes));

            //合并接口
            NodeList<ClassOrInterfaceType> implementedTypes = ((ClassOrInterfaceDeclaration) newTypeDeclaration).getImplementedTypes();
            implementedTypes.addAll(((ClassOrInterfaceDeclaration) oldTypeDeclaration).getImplementedTypes());
            Set<ClassOrInterfaceType> implementedTypesSet = new HashSet<>(implementedTypes);
            ((ClassOrInterfaceDeclaration) newTypeDeclaration).setImplementedTypes(new NodeList<>(implementedTypesSet));

            //合并字段
            List<FieldDeclaration> oldFields = oldTypeDeclaration.getFields();
            for (FieldDeclaration field : oldFields) {
                if (!field.toString().contains(MergeConstants.NEW_ELEMENT_TAG)) {
                    newTypeDeclaration.addMember(field);
                }
            }

            //合并方法
            List<MethodDeclaration> oldMethods = oldTypeDeclaration.getMethods();
            for (MethodDeclaration method : oldMethods) {
                if (!method.toString().contains(MergeConstants.NEW_ELEMENT_TAG)) {
                    newTypeDeclaration.addMember(method);
                }
            }
        }

        return newCompilationUnit;
    }

}
