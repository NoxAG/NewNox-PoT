package com.noxag.newnox.pot.userinterface;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;

import com.noxag.newnox.pot.userinterface.pdfmodule.PDFPageDrawer;
import com.noxag.newnox.pot.userinterface.pdfmodule.PDFView;
import com.noxag.newnox.pot.util.PDFUtil;
import com.noxag.newnox.pot.util.TextPositionSequence;

public class MainWindow extends JFrame {

    private static final long serialVersionUID = -8163834508651398652L;
    private JButton openFileButton;
    private JButton searchButton;
    private JPanel searchBar;
    private JTextField searchField;
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

        openFileButton = new JButton("Open File");
        searchButton = new JButton("Find");

        searchField = new JTextField("");
        searchField.setColumns(20);
        searchBar = new JPanel();

        searchBar.setLayout(new FlowLayout());
        searchBar.add(searchField);
        searchBar.add(searchButton);

        fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF", "pdf"));

        pdfViewPanel = new PDFView();

        pdfScrollPane = new JScrollPane(pdfViewPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        pdfScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        this.add(pdfScrollPane, BorderLayout.CENTER);
        this.add(openFileButton, BorderLayout.PAGE_START);
        this.add(searchBar, BorderLayout.PAGE_END);

        openFileButton.addActionListener(this::openFileChooser);
        searchButton.addActionListener(this::searchButtonAction);
        searchField.addActionListener(this::searchButtonAction);

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

    private void searchButtonAction(ActionEvent e) {
        String searchText = this.searchField.getText();
        if (this.pdfDocument != null) {
            try {
                for (int pageNum = 1; pageNum < this.pdfDocument.getNumberOfPages(); pageNum++) {
                    List<PDAnnotation> pageAnnotations = this.pdfDocument.getPage(pageNum - 1).getAnnotations();

                    for (TextPositionSequence finding : PDFUtil.findSubwords(this.pdfDocument, pageNum, searchText)) {

                        // Now add the markup annotation, a highlight to PDFBox
                        PDAnnotationTextMarkup txtMark = new PDAnnotationTextMarkup(
                                PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT);
                        txtMark.setColor(new PDColor(new float[] { 1, 1, 0 }, PDDeviceRGB.INSTANCE));
                        txtMark.setConstantOpacity((float) 0.5);
                        txtMark.setRectangle(new PDRectangle(finding.getX(), finding.getY(), finding.getWidth(),
                                finding.getHeight()));
                        pageAnnotations.add(txtMark);

                        System.out.println("txtMark = " + txtMark.getRectangle().getLowerLeftX());
                        System.out.println("txtMark = " + txtMark.getRectangle().getLowerLeftY());
                        System.out.println("txtMark = " + txtMark.getRectangle().getUpperRightX());
                        System.out.println("txtMark = " + txtMark.getRectangle().getUpperRightY());
                    }
                }

                initiatePDFView();

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "You need to open a PDF before you can search for something");
        }
    }

    private void openFileChooser(ActionEvent e) {
        int returnState = fileChooser.showOpenDialog(null);

        if (returnState == JFileChooser.APPROVE_OPTION) {
            System.out.println("Die zu öffnende Datei ist: " + fileChooser.getSelectedFile().getAbsolutePath());
            try {
                if (pdfDocument != null) {
                    pdfDocument.close();
                }
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
        this.pdfViewPanel.setScaleFactor(0.4);
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
}
