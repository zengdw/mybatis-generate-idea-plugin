package com.zengdw.mybatis.override;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

/**
 * @author zengd
 * @version 1.0
 * @date 2023/3/22 15:37
 */
public class MyFileChooserDescriptor extends FileChooserDescriptor {
    private Condition<? super VirtualFile> myFolderFilter = null;

    public MyFileChooserDescriptor(boolean chooseFiles, boolean chooseFolders, boolean chooseJars, boolean chooseJarsAsFiles, boolean chooseJarContents, boolean chooseMultiple) {
        super(chooseFiles, chooseFolders, chooseJars, chooseJarsAsFiles, chooseJarContents, chooseMultiple);
    }

    public FileChooserDescriptor withFolderFilter(@Nullable Condition<? super VirtualFile> filter) {
        myFolderFilter = filter;
        return this;
    }

    @Override
    public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
        boolean visible = super.isFileVisible(file, showHiddenFiles);

        if (visible && file.isDirectory() && myFolderFilter != null) {
            return myFolderFilter.value(file);
        }

        return visible;
    }

    @Override
    public boolean isFileSelectable(VirtualFile file) {
        boolean selectable = super.isFileSelectable(file);
        if (selectable && file.isDirectory() && myFolderFilter != null) {
            return myFolderFilter.value(file);
        }
        return selectable;
    }
}
