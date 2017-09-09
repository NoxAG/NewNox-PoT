package com.noxag.newnox.pot.userinterface;

import java.awt.BorderLayout;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class FileChooser extends JFrame {

    private static final long serialVersionUID = -6243001352460084515L;

    public FileChooser() {
        JFileChooser jFileChooser = new javax.swing.JFileChooser();
        this.setLayout(new BorderLayout());
        this.add(jFileChooser, BorderLayout.CENTER);
    }
}
