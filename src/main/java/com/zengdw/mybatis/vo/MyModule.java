package com.zengdw.mybatis.vo;

/**
 * @author zengd
 * @version 1.0
 * @date 2023/3/20 11:13
 */
public class MyModule {
    private String name;
    private String path;

    public MyModule(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return name;
    }
}
