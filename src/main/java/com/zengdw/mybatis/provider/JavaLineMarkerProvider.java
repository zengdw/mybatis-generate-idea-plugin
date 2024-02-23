package com.zengdw.mybatis.provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.xml.DomElement;
import com.zengdw.mybatis.util.MapperUtils;
import com.zengdw.mybatis.xml.dom.Mapper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * java类行标记, 并提供对这些应用的导航。
 *
 * @author zengd
 * @date 2023/10/16 14:47
 */
public class JavaLineMarkerProvider extends RelatedItemLineMarkerProvider {
    static Icon PANDA = IconLoader.getIcon("/images/right.svg", JavaLineMarkerProvider.class);

    /**
     * collectNavigationMarkers 方法的入参数为 PsiElement 与一个集合
     * 若是我们想在当前 PsiElement 所在的编辑器行中跳转行标识符，则在第二个集合参数中
     * 添加可跳转的目标
     */
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (element instanceof PsiClass) {
            psiClassMarker((PsiClass) element, result);
        }
        if (element instanceof PsiMethod) {
            psiMethodMarker((PsiMethod) element, result);
        }
    }

    private void psiMethodMarker(PsiMethod psiMethod, Collection<? super RelatedItemLineMarkerInfo<PsiElement>> result) {
        Project project = psiMethod.getProject();
        /*VirtualFile virtualFile = psiMethod.getContainingFile().getVirtualFile();
        Collection<VirtualFile> virtualFiles = FilenameIndex.getVirtualFilesByName(project, virtualFile.getName().split("\\.")[0] + ".xml", GlobalSearchScope.projectScope(project));
        List<PsiFile> psiFiles = PsiUtilCore.toPsiFiles(PsiManager.getInstance(project), virtualFiles);
        if (psiFiles.isEmpty()) return;
        List<PsiElement> targets = new ArrayList<>();
        for (PsiFile psiFile : psiFiles) {
            XmlFile xmlFile = (XmlFile) psiFile;
            List<XmlTag> tagList = Arrays.stream(xmlFile.getRootTag().getSubTags()).filter(xmlTag -> xmlTag.getAttribute("id").getValue().equals(psiMethod.getName())).collect(Collectors.toList());
            targets.addAll(tagList);
        }*/
        Collection<XmlElement> tags = MapperUtils.findTags(project, psiMethod);
        if (tags.isEmpty()) return;
        NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(PANDA)
                .setTargets(tags)
                .setTooltipText("跳转到 xml 文件")
                .setAlignment(GutterIconRenderer.Alignment.RIGHT);
        if (null != psiMethod.getNameIdentifier()) {
            result.add(builder.createLineMarkerInfo(psiMethod.getNameIdentifier()));
        }
    }

    private void psiClassMarker(PsiClass psiClass, @NotNull Collection<? super RelatedItemLineMarkerInfo<PsiElement>> result) {
        Project project = psiClass.getProject();
        // 1. 查找与 class 类名同名的 xml 文件, 并追加到结果集合
        /*Collection<VirtualFile> virtualFiles = FilenameIndex.getVirtualFilesByName(project, psiClass.getName() + ".xml", GlobalSearchScope.projectScope(project));
        List<PsiFile> xmlFile = PsiUtilCore.toPsiFiles(PsiManager.getInstance(project), virtualFiles);
        if (xmlFile.isEmpty()) return;*/

        Collection<Mapper> mappers = MapperUtils.findMappers(project, psiClass);
        if (mappers.isEmpty()) return;

        // 通过 NavigationGutterIconBuilder 构造跳转的信息，包括：
        // create 方法传入了行标识符的 Icon
        // setTargets 为跳转的目标，可能有多个，因此为一个 List
        // setTooltipText 方法设置了标识符的提示文本
        NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(PANDA)
                .setTargets(mappers.stream().map(DomElement::getXmlElement).collect(Collectors.toList()))
                .setTooltipText("跳转到 xml 文件")
                .setAlignment(GutterIconRenderer.Alignment.RIGHT);
        if (null != psiClass.getNameIdentifier()) {
            result.add(builder.createLineMarkerInfo(psiClass.getNameIdentifier()));
        }
    }
}
