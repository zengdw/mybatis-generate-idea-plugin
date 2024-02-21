package com.zengdw.mybatis.contributor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.InitialPatternCondition;
import com.intellij.patterns.XmlAttributeValuePattern;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.xml.XmlAttributeImpl;
import com.intellij.psi.impl.source.xml.XmlAttributeValueImpl;
import com.intellij.psi.impl.source.xml.XmlFileImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.zengdw.mybatis.util.MapperUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 为xml tag添加引用
 *
 * @author zengd
 * @date 2023/10/17 14:24
 */
public class XmlRefContributor extends PsiReferenceContributor {
    private final XmlAttributeValuePattern pattern = new XmlAttributeValuePattern(new InitialPatternCondition<>(XmlAttributeValue.class) {
        @Override
        public boolean accepts(@Nullable final Object o, final ProcessingContext context) {
            if (!(o instanceof XmlAttributeValue)) return false;
            PsiElement parent = ((XmlAttributeValueImpl) o).getParent();
            if (!(parent instanceof XmlAttributeImpl)) return false;
            XmlAttributeImpl attribute = (XmlAttributeImpl) parent;
            if (!"id".equals(attribute.getName())) return false;
            if (attribute.getParent() == null) return false;
            return MapperUtils.isElementWithinMybatisFile(attribute.getParent());
        }
    });

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(pattern,
                new PsiReferenceProvider() {
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        XmlAttributeValue attributeValue = (XmlAttributeValue) element;
                        Project project = element.getProject();
                        PsiFile psiFile = element.getContainingFile();
                        XmlTag rootTag = ((XmlFileImpl) psiFile).getRootTag();
                        String classRefName = null;
                        if (rootTag != null) {
                            classRefName = rootTag.getAttributeValue("namespace");
                        }
                        PsiClass psiClass = null;
                        if (classRefName != null) {
                            psiClass = JavaPsiFacade.getInstance(project).findClass(classRefName, GlobalSearchScope.projectScope(project));
                        }
                        PsiMethod[] methods = new PsiMethod[0];
                        if (psiClass != null) {
                            methods = psiClass.findMethodsByName(attributeValue.getValue(), false);
                        }
                        PsiReference[] psiReferences = new PsiReference[methods.length];
                        for (int i = 0; i < methods.length; i++) {
                            psiReferences[i] = new PsiRef(element, new TextRange(1, attributeValue.getValue().length() + 1), methods[i]);
                        }
                        return psiReferences;
                    }
                });
    }

    static class PsiRef extends PsiReferenceBase<PsiElement> {
        PsiMethod psiMethod;

        public PsiRef(@NotNull PsiElement element, TextRange rangeInElement, PsiMethod psiMethod) {
            super(element, rangeInElement);
            this.psiMethod = psiMethod;
        }

        @Override
        public @Nullable PsiElement resolve() {
            return psiMethod;
        }
    }
}
