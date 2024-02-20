package com.zengdw.mybatis.xml;

import com.intellij.util.xml.DomFileDescription;
import com.zengdw.mybatis.xml.dom.Mapper;

/**
 * @author zengd
 * @date 2023/10/18 15:50
 */
public class MapperDomFileDescription extends DomFileDescription<Mapper> {
    public static final String[] HTTP_MYBATIS_ORG_DTD_MYBATIS_3_MAPPER_DTD =
            new String[]{"http://mybatis.org/dtd/mybatis-3-mapper.dtd",
                    "http://www.mybatis.org/dtd/mybatis-3-mapper.dtd",
                    "https://mybatis.org/dtd/mybatis-3-mapper.dtd",
                    "https://www.mybatis.org/dtd/mybatis-3-mapper.dtd"
            };
    public MapperDomFileDescription() {
        super(Mapper.class, "mapper");
    }

    @Override
    protected void initializeFileDescription() {
        registerNamespacePolicy("MybatisXml", HTTP_MYBATIS_ORG_DTD_MYBATIS_3_MAPPER_DTD);
    }
}
