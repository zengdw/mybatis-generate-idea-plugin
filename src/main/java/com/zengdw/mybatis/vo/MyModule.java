package com.zengdw.mybatis.vo;

import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MyModule)) return false;
        MyModule myModule = (MyModule) o;
        return Objects.equals(name, myModule.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
