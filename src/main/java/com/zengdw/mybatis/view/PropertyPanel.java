package com.zengdw.mybatis.view;

import javax.swing.*;
import java.awt.*;

/**
 * @author zengd
 * @version 1.0
 * @date 2023/3/17 16:43
 */
public class PropertyPanel extends JPanel {

    public PropertyPanel() {
        setPreferredSize(new Dimension(320, 240));

        add(getBaseFilePanel());
    }

    private JPanel getBaseFilePanel() {
        JPanel baseFile = new JPanel();
        baseFile.setBorder(BorderFactory.createTitledBorder("baseFile"));

        Box box = new Box(BoxLayout.X_AXIS);
        JLabel javaModelPath = new JLabel("java model path");
        JTextField jTextField = new JTextField("src/java/main");
        box.add(javaModelPath);
        box.add(jTextField);
        baseFile.add(box);

        Box box1 = new Box(BoxLayout.X_AXIS);
        JLabel javaModelPackage = new JLabel("java model package");
        JTextField jTextField1 = new JTextField();
        box1.add(javaModelPackage);
        box1.add(jTextField1);
        baseFile.add(box1);

        return baseFile;
    }


}
