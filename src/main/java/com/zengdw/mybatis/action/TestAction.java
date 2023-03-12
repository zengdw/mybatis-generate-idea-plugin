package com.zengdw.mybatis.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class TestAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Thread thread = Thread.currentThread();
        ClassLoader originalClassLoader = thread.getContextClassLoader();
        ClassLoader pluginClassLoader = this.getClass().getClassLoader();
        System.out.println(3);
    }
}
