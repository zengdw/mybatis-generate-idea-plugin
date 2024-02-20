package com.zengdw.mybatis.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.zengdw.mybatis.xml.dom.IdDomElement;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author zengd
 * @date 2023/10/19 9:18
 */
public class JavaUtils {
    public static boolean isElementWithinInterface(@Nullable PsiElement element) {
        if (element instanceof PsiClass && ((PsiClass) element).isInterface()) {
            return true;
        }
        PsiClass type = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return Optional.ofNullable(type).isPresent() && type.isInterface();
    }

    public static PsiMethod[] findMethods(@NotNull Project project, @Nullable String clazzName, @Nullable String methodName) {
        if (StringUtils.isBlank(clazzName) && StringUtils.isBlank(methodName)) {
            return new PsiMethod[0];
        }
        PsiClass[] classes = findClasses(project, clazzName);

        return Arrays.stream(classes)
                .map(psiClass -> psiClass.findMethodsByName(methodName, true))
                .flatMap(Arrays::stream).toArray(PsiMethod[]::new);
    }

    public static PsiClass[] findClasses(@NotNull Project project, @NotNull String clazzName) {
        return JavaPsiFacade.getInstance(project).findClasses(clazzName, GlobalSearchScope.allScope(project));
    }

    public static Optional<PsiMethod> findMethod(@NotNull Project project, @NotNull IdDomElement element) {
        return findMethod(project, MapperUtils.getNamespace(element), MapperUtils.getId(element));
    }

    public static Optional<PsiMethod> findMethod(@NotNull Project project, @Nullable String clazzName, @Nullable String methodName) {
        if (StringUtils.isBlank(clazzName) && StringUtils.isBlank(methodName)) {
            return Optional.empty();
        }
        Optional<PsiClass> clazz = findClazz(project, clazzName);
        if (clazz.isPresent()) {
            PsiMethod[] methods = clazz.get().findMethodsByName(methodName, true);
            return ArrayUtils.isEmpty(methods) ? Optional.empty() : Optional.of(methods[0]);
        }
        return Optional.empty();
    }

    public static Optional<PsiClass> findClazz(@NotNull Project project, @NotNull String clazzName) {
        String classNameNeedFind = clazzName;
        if (classNameNeedFind.contains("$")) {
            classNameNeedFind = classNameNeedFind.replace("$", ".");
        }
        final JavaPsiFacade instance = JavaPsiFacade.getInstance(project);
        return Optional.ofNullable(instance.findClass(classNameNeedFind, GlobalSearchScope.allScope(project)));
    }

}
