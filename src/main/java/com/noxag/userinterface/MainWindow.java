package com.noxag.userinterface;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class MainWindow extends JFrame {

    private static final long serialVersionUID = -8163834508651398652L;

    public MainWindow() {
        init();

        this.setVisible(true);
    }

    private void init() {
        this.setBounds(getDefaultBounds());

    }

    private Rectangle getDefaultBounds() {
        return getRelativeBounds(0.5, 0.5, 0.5, 0.5);
    }

    private Rectangle getRelativeBounds(double relativeX, double relativeY, double relativeWidth,
            double relativeHeight) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int width = (int) (screenSize.getWidth() * relativeWidth);
        int height = (int) (screenSize.getHeight() * relativeHeight);
        int posX = (int) ((screenSize.getWidth() * relativeX) - width / 2);
        int posY = (int) ((screenSize.getHeight() * relativeY) - height / 2);

        return new Rectangle(posX, posY, width, height);
    }

}
