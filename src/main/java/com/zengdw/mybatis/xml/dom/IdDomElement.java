package com.zengdw.mybatis.xml.dom;

import com.intellij.util.xml.*;

/**
 * @author zengd
 * @date 2023/10/18 17:27
 */
@NameStrategy(JavaNameStrategy.class)
public interface IdDomElement extends DomElement {

    @Attribute("id")
//    @Convert(PropertyConverter.class)
    GenericAttributeValue<Object> getId();
}
