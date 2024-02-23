package com.zengdw.mybatis.intention;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import com.zengdw.mybatis.util.MapperUtils;
import com.zengdw.mybatis.xml.dom.Mapper;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据方法名称自动生成xml方法
 *
 * @author zengd
 * @date 2024/2/23 13:48
 */
public abstract class AbstractIntentionAction extends PsiElementBaseIntentionAction {
    protected List<String> insertTags = List.of("insert", "add", "create", "save");
    protected List<String> deleteTags = List.of("delete", "remove", "del");
    protected List<String> updateTags = List.of("update", "modify", "edit", "change", "alter", "replace");
    protected List<String> selectTags = List.of("search", "query", "select", "find", "get", "load");

    private final Map<String, List<String>> tagsMap = new HashMap<>() {{
        put("insert", insertTags);
        put("delete", deleteTags);
        put("update", updateTags);
        put("select", selectTags);
    }};

    protected boolean methodStart(String methodName, String type) {
        for (String s : tagsMap.get(type)) {
            if (methodName.startsWith(s)) return true;
        }
        return false;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) throws IncorrectOperationException {
        // 在这里编写你的操作逻辑，当用户按下Alt+Enter时触发
        PsiMethod method = (PsiMethod) psiElement.getParent();
        PsiClass psiClass = (PsiClass) method.getParent();

        List<Mapper> mappers = MapperUtils.findMappers(project, psiClass);
        PsiFile psiFile = mappers.get(0).getXmlElement().getContainingFile();
        XmlFile xmlFile = (XmlFile) psiFile;
        XmlDocument document = xmlFile.getDocument();
        if (document != null) {
            XmlElementFactory elementFactory = XmlElementFactory.getInstance(project);
            XmlTag xmlTag = elementFactory.createTagFromText(xmlTagStr(method), document.getLanguage());
            // 使用WriteCommandAction进行安全的异步修改（避免数据同步问题）
            ApplicationManager.getApplication().invokeAndWait(() -> WriteCommandAction.runWriteCommandAction(psiElement.getProject(), () -> {
                document.getRootTag().addSubTag(xmlTag, false);

                // 确保所有更改已提交到文档
                PsiDocumentManager.getInstance(psiElement.getProject()).commitAllDocuments();
            }));

            VirtualFile virtualFile = psiFile.getVirtualFile();
            // 打开指定文件并定位到编辑器
            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            OpenFileDescriptor descriptor = new OpenFileDescriptor(project, virtualFile);
            fileEditorManager.openTextEditor(descriptor, true);
        }
    }

    protected abstract String xmlTagStr(PsiMethod method);

    /**
     * 返回一个布尔值，表示该操作在此上下文中是否可用
     */
    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) {
        PsiElement psiMethod = psiElement.getParent();
        if (!(psiMethod instanceof PsiMethod)) return false;
        PsiElement psiClass = psiMethod.getParent();
        if (!(psiClass instanceof PsiClass)) return false;

        Collection<Mapper> mappers = MapperUtils.findMappers(project, (PsiClass) psiClass);
        if (mappers.isEmpty()) return false;

        Collection<XmlElement> tags = MapperUtils.findTags(project, (PsiMethod) psiMethod);
        return tags.isEmpty();
    }

    /**
     * 返回一个描述此类快捷操作所属组别的字符串
     * 多个意图操作只有返回相同字符串就可以属于同一个组
     */
    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "generateXmlMethod";
    }

}
