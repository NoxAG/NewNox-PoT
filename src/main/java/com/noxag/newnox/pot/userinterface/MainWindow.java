package com.noxag.newnox.pot.userinterface;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.noxag.newnox.pot.userinterface.pdfmodule.PDFPageDrawer;
import com.noxag.newnox.pot.userinterface.pdfmodule.PDFView;

public class MainWindow extends JFrame {

    private static final long serialVersionUID = -8163834508651398652L;
    private JButton openFileButton;
    private PDFView pdfViewPanel;
    private JFileChooser fileChooser;
    private PDDocument pdfDocument;
    private JScrollPane pdfScrollPane;

    public MainWindow() {
        init();

        this.setVisible(true);
    }

    private void init() {
        this.setBounds(getDefaultBounds());
        this.setLayout(new BorderLayout());

        fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF", "pdf"));

        pdfViewPanel = new PDFView();

        pdfScrollPane = new JScrollPane(pdfViewPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        this.add(pdfScrollPane, BorderLayout.CENTER);

        openFileButton = new JButton("Open File");
        this.add(openFileButton, BorderLayout.PAGE_START);
        openFileButton.addActionListener(this::openFileChooser);

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    pdfDocument.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("closing");
                    System.exit(0);
                }
            }
        });

        pdfScrollPane.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                updatePDFView();
            }
        });

    }

    private void openFileChooser(ActionEvent e) {
        int returnState = fileChooser.showOpenDialog(null);

        if (returnState == JFileChooser.APPROVE_OPTION) {
            System.out.println("Die zu öffnende Datei ist: " + fileChooser.getSelectedFile().getAbsolutePath());
            try {
                pdfDocument = PDDocument.load(new File(fileChooser.getSelectedFile().getAbsolutePath()));
                initiatePDFView();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void initiatePDFView() {
        this.pdfViewPanel.setPDFImage(PDFPageDrawer.getAllPagesFromPDFAsImage(this.pdfDocument));
        // this.pdfViewPanel.addPDFImage(CustomPageDrawer.getBufferedImageByPDF(this.pdfDocument));
        this.updatePDFView();
    }

    private void updatePDFView() {
        this.pdfViewPanel.setScaleFactor(0.8);
        this.pdfViewPanel.rescale();

        this.pdfScrollPane.getViewport().revalidate();
    }

    private Rectangle getDefaultBounds() {
        return getRelativeBoundFromScreenSize(0.5, 0.5, 0.5, 0.5);
    }

    private Rectangle getRelativeBoundFromScreenSize(double relativeX, double relativeY, double relativeWidth,
            double relativeHeight) {
        return getRelativeBound(Toolkit.getDefaultToolkit().getScreenSize(), relativeX, relativeY, relativeWidth,
                relativeHeight);
    }

    private Rectangle getRelativeBound(Dimension dimension, double relativeX, double relativeY, double relativeWidth,
            double relativeHeight) {
        int width = (int) (dimension.getWidth() * relativeWidth);
        int height = (int) (dimension.getHeight() * relativeHeight);
        int posX = (int) ((dimension.getWidth() * relativeX) - width / 2);
        int posY = (int) ((dimension.getHeight() * relativeY) - height / 2);

        return new Rectangle(posX, posY, width, height);
    }

    private Dimension getRelativeSize(Dimension originSize, double relativeWidth, double relativeHeight) {
        return new Dimension((int) (originSize.getWidth() * relativeWidth),
                (int) (originSize.getHeight() * relativeHeight));
    }

}
