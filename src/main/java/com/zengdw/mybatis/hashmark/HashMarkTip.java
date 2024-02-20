package com.zengdw.mybatis.hashmark;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.zengdw.mybatis.xml.dom.Mapper;

public interface HashMarkTip {
    String getName();

    void tipValue(CompletionResultSet completionResultSet, Mapper mapper);
}
