package com.zengdw.mybatis.vo;

import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

/**
 * @author zengd
 * @version 1.0
 * @date 2023/3/22 11:22
 */
public class MyModule {
    private final String name;
    private final Module module;

    public MyModule(@NotNull String name, Module module) {
        this.name = name;
        this.module = module;
    }

    public String getName() {
        return name;
    }

    public Module getModule() {
        return module;
    }

    @Override
    public String toString() {
        return name;
    }
}
