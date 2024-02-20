package com.zengdw.mybatis.provider;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.searches.DefinitionsScopedSearch;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.Processor;
import com.zengdw.mybatis.util.MapperUtils;
import com.zengdw.mybatis.xml.dom.Mapper;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * 定义Ctrl + Alt + B跳转
 *
 * @author zengd
 */
public class MapperClassSearch extends QueryExecutorBase<PsiElement, DefinitionsScopedSearch.SearchParameters> {
    /**
     * 必须是只读的操作, 否则无法读取java的类
     */
    public MapperClassSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull DefinitionsScopedSearch.SearchParameters queryParameters, @NotNull Processor<? super PsiElement> consumer) {
        final PsiElement element = queryParameters.getElement();
        if (element instanceof PsiMethod psiMethod) {
            final Collection<XmlElement> tags = MapperUtils.findTags(psiMethod.getProject(), psiMethod);
            for (XmlElement idDomElement : tags) {
                consumer.process(idDomElement);
            }
        }
        if (element instanceof PsiClass) {
            Collection<Mapper> mappers = MapperUtils.findMappers(queryParameters.getProject(), (PsiClass) element);
            for (Mapper mapper : mappers) {
                consumer.process(mapper.getXmlElement());
            }
        }

    }


}
