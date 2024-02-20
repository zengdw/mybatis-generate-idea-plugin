package com.zengdw.mybatis.xml.dom;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.*;

import java.util.List;

/**
 * @author zengd
 * @date 2023/10/18 15:13
 */
@Namespace("MybatisXml")
public interface Mapper extends DomElement {
    @SubTagsList({"insert", "update", "delete", "select"})
    List<IdDomElement> getDaoElements();

    @Attribute("namespace")
    GenericAttributeValue<PsiClass> getNamespace();

    @SubTagList("insert")
    List<Insert> getInserts();

    @SubTagList("update")
    List<Update> getUpdates();

    @SubTagList("delete")
    List<Delete> getDeletes();

    @SubTagList("select")
    List<Select> getSelects();
}
