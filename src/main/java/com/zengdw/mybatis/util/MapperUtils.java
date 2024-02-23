package com.zengdw.mybatis.util;

import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericAttributeValue;
import com.zengdw.mybatis.xml.dom.IdDomElement;
import com.zengdw.mybatis.xml.dom.Mapper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author zengd
 * @date 2023/10/19 9:15
 */
public class MapperUtils {
    public static <T extends IdDomElement> String getId(@NotNull T domElement) {
        return domElement.getId().getRawText();
    }

    public static boolean isElementWithinMybatisFile(@NotNull PsiElement element) {
        PsiFile psiFile = element.getContainingFile();
        return element instanceof XmlElement && DomUtils.isMybatisFile(psiFile);
    }

    public static Collection<XmlElement> findTags(@NotNull Project project, @NotNull PsiMethod method) {
        final PsiClass containingClass = method.getContainingClass();
        if (containingClass == null) {
            return Collections.emptyList();
        }
        List<XmlElement> result = Lists.newArrayList();
        for (Mapper mapper : findMappers(project)) {
            final GenericAttributeValue<PsiClass> namespace = mapper.getNamespace();
            final PsiClass namespaceValue = namespace.getValue();
            if (namespaceValue == null) {
                continue;
            }
            if (namespaceValue.equals(containingClass) ||
                    namespaceValue.isInheritor(containingClass, true)) {
                final Optional<IdDomElement> first = mapper.getDaoElements().stream()
                        .filter(item -> method.getName().equals(item.getId().getStringValue()))
                        .findFirst();
                first.ifPresent(item -> result.add(item.getXmlElement()));
            }
        }
        return result;
    }

    public static List<Mapper> findMappers(@NotNull Project project) {
        return DomUtils.findDomElements(project, Mapper.class);
    }

    public static List<Mapper> findMappers(Project project, PsiClass clazz) {
        List<Mapper> mappers = null;
        if (project == null || clazz == null) {
            mappers = Collections.emptyList();
        }
        if (mappers == null && StringUtils.isNotBlank(clazz.getQualifiedName()) && JavaUtils.isElementWithinInterface(clazz)) {
            mappers = findMappers(project, Objects.requireNonNull(clazz.getQualifiedName()));
        }
        if (mappers == null) {
            mappers = Collections.emptyList();
        }
        return mappers;
    }

    public static List<Mapper> findMappers(@NotNull Project project, @NotNull String namespace) {
        List<Mapper> result = Lists.newArrayList();
        for (Mapper mapper : findMappers(project)) {
            if (getNamespace(mapper).equals(namespace)) {
                result.add(mapper);
            }
        }
        return result;
    }

    public static String getNamespace(@NotNull Mapper mapper) {
        String ns = mapper.getNamespace().getStringValue();
        return null == ns ? "" : ns;
    }

    public static String getNamespace(@NotNull DomElement element) {
        return getNamespace(getMapper(element));
    }

    public static Mapper getMapper(@NotNull DomElement element) {
        Optional<Mapper> optional = Optional.ofNullable(DomUtil.getParentOfType(element, Mapper.class, true));
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new IllegalArgumentException("Unknown element");
        }
    }

    public static Optional<IdDomElement> findParentIdDomElement(@Nullable PsiElement element) {
        DomElement domElement = DomUtil.getDomElement(element);
        if (null == domElement) {
            return Optional.empty();
        }
        if (domElement instanceof IdDomElement) {
            return Optional.of((IdDomElement) domElement);
        }
        return Optional.ofNullable(DomUtil.getParentOfType(domElement, IdDomElement.class, true));
    }

}
