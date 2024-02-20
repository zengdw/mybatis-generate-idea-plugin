package com.zengdw.mybatis.hashmark;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.zengdw.mybatis.xml.dom.Mapper;

public class JdbcTypeNameHashMarkTip implements HashMarkTip {
    @Override
    public String getName() {
        return "jdbcTypeName";
    }

    /**
     * 自定义结构体类型,无需支持
     *
     * @param completionResultSet
     * @param mapper
     */
    @Override
    public void tipValue(CompletionResultSet completionResultSet, Mapper mapper) {

    }
}
