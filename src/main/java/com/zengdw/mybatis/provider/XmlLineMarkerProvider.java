package com.zengdw.mybatis.provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.zengdw.mybatis.util.JavaUtils;
import com.zengdw.mybatis.util.MapperUtils;
import com.zengdw.mybatis.xml.dom.IdDomElement;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * xml文件行标记, 并提供对这些应用的导航。
 *
 * @author zengd
 * @date 2023/10/16 14:47
 */
public class XmlLineMarkerProvider extends RelatedItemLineMarkerProvider {
    private final List<String> tags = Arrays.asList("mapper", "insert", "delete", "update", "select");

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (!(element instanceof XmlToken && isTag(element) && MapperUtils.isElementWithinMybatisFile(element))) return;

        DomElement domElement = DomUtil.getDomElement(element);
        if (domElement == null) return;

        PsiElement[] targets;
        if (domElement instanceof IdDomElement) {
            targets = JavaUtils.findMethods(element.getProject(),
                    MapperUtils.getNamespace(domElement),
                    MapperUtils.getId((IdDomElement) domElement));
        } else {
            XmlTag xmlTag = domElement.getXmlTag();
            if (xmlTag == null) {
                return;
            }
            String namespace = xmlTag.getAttributeValue("namespace");
            if (StringUtils.isEmpty(namespace)) {
                return;
            }
            targets = JavaUtils.findClasses(element.getProject(), namespace);
        }
        NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(JavaLineMarkerProvider.PANDA)
                .setTargets(targets)
                .setAlignment(GutterIconRenderer.Alignment.RIGHT);
        result.add(builder.createLineMarkerInfo(element));
    }

    private boolean isTag(PsiElement element) {
        XmlToken xmlToken = (XmlToken) element;
        String text = xmlToken.getText();
        if ("mapper".equals(text)) {
            PsiElement nextSibling = xmlToken.getNextSibling();
            return nextSibling instanceof PsiWhiteSpace;
        }
        if (tags.contains(text)) {
            PsiElement parent = xmlToken.getParent();
            if (parent instanceof XmlTag) {
                return xmlToken.getNextSibling() instanceof PsiWhiteSpace;
            }
        }
        return false;
    }
}
