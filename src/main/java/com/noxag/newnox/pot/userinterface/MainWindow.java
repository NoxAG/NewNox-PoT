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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.noxag.newnox.pot.userinterface.pdfmodule.PDFPageRenderer;
import com.noxag.newnox.pot.util.PDFHighlighter;
import com.noxag.newnox.pot.util.PDFTextAnalyzerUtil;
import com.noxag.newnox.pot.util.PDFTextExtractionUtil;
import com.noxag.newnox.pot.util.data.PDFLine;
import com.noxag.newnox.pot.util.data.PDFPage;

public class MainWindow extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(MainWindow.class.getName());

    private static final long serialVersionUID = -8163834508651398652L;

    private JButton openFileButton;
    private JButton searchButton;
    private JButton selectAllButton;
    private JButton selectContentPagesButton;
    private JPanel searchBar;
    private JTextField searchField;
    private PDFView pdfViewPanel;
    private JFileChooser fileChooser;
    private JScrollPane pdfScrollPane;
    private transient PDDocument pdfDocument;

    public MainWindow() {
        init();

        this.setVisible(true);
    }

    private void init() {
        this.setBounds(getDefaultBounds());
        this.setLayout(new BorderLayout());

        openFileButton = new JButton("Open File");
        searchButton = new JButton("Search");
        selectAllButton = new JButton("All Pages");
        selectContentPagesButton = new JButton("Content Pages");

        searchField = new JTextField("");
        searchField.setColumns(20);
        searchBar = new JPanel();

        searchBar.setLayout(new FlowLayout());
        searchBar.add(searchField);
        searchBar.add(searchButton);
        searchBar.add(selectAllButton);
        searchBar.add(selectContentPagesButton);

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
        selectAllButton.addActionListener(this::selectAllButtonAction);
        selectContentPagesButton.addActionListener(this::selectContentPagesButtonAction);
        searchButton.addActionListener(this::searchButtonAction);

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    pdfDocument.close();
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "PDF document could not be closed properly", e);
                } finally {
                    LOGGER.log(Level.INFO, "PDF document has been closed");
                    System.exit(0);
                }
            }
        });

        pdfScrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                updatePDFView();
            }
        });
    }

    private void searchButtonAction(ActionEvent e) {
        PDFHighlighter.clearDocumentFromAnnotations(this.pdfDocument);

        if (this.pdfDocument == null) {
            JOptionPane.showMessageDialog(this, "You need to open a PDF before you can search for something");
            return;
        }

        String searchTerm = searchField.getText();
        try {
            PDFHighlighter.highlight(this.pdfDocument, PDFTextExtractionUtil.getTextFindings(PDFTextExtractionUtil
                    .findWord(this.pdfDocument, searchTerm, PDFTextExtractionUtil::findCharSequence)));
        } catch (IOException ioE) {
            LOGGER.log(Level.WARNING, "PDF Document could not be searched through", ioE);
        }
        updatePDFView();
    }

    private void selectContentPagesButtonAction(ActionEvent e) {
        PDFHighlighter.clearDocumentFromAnnotations(this.pdfDocument);
        if (this.pdfDocument == null) {
            JOptionPane.showMessageDialog(this, "You need to open a PDF before you can search for something");
            return;
        }
        try {
            List<PDFLine> lines = PDFTextAnalyzerUtil
                    .reduceToLines(PDFTextExtractionUtil.getCompleteText(this.pdfDocument));
            PDFHighlighter.highlight(this.pdfDocument,
                    PDFTextExtractionUtil.getTextFindings(PDFTextAnalyzerUtil.reduceToTextPositions(lines)));
        } catch (IOException ioE) {
            LOGGER.log(Level.WARNING, "PDF Document could not be searched through", ioE);
        }

        updatePDFView();
    }

    private void selectAllButtonAction(ActionEvent e) {
        PDFHighlighter.clearDocumentFromAnnotations(this.pdfDocument);

        if (this.pdfDocument == null) {
            JOptionPane.showMessageDialog(this, "You need to open a PDF before you can search for something");
            return;
        }

        try {
            List<PDFPage> pages = PDFTextExtractionUtil.getCompleteText(this.pdfDocument);
            PDFHighlighter.highlight(this.pdfDocument,
                    PDFTextExtractionUtil.getTextFindings(PDFTextAnalyzerUtil.reduceToWords(pages)));
        } catch (IOException ioE) {
            LOGGER.log(Level.WARNING, "PDF Document could not be searched through", ioE);
        }
        updatePDFView();
    }

    private void openFileChooser(ActionEvent e) {
        int returnState = fileChooser.showOpenDialog(null);

        if (returnState == JFileChooser.APPROVE_OPTION) {
            LOGGER.log(Level.INFO, "New Filepath has been chosen: " + fileChooser.getSelectedFile().getAbsolutePath());
            try {
                if (pdfDocument != null) {
                    pdfDocument.close();
                }
                pdfDocument = PDDocument.load(new File(fileChooser.getSelectedFile().getAbsolutePath()));
                initiatePDFView();
            } catch (IOException e1) {
                LOGGER.log(Level.WARNING, "PDF document could not be loaded", e1);
            }
        }
    }

    private void initiatePDFView() {
        this.pdfViewPanel.setPDFImage(PDFPageRenderer.getAllPagesFromPDFAsImage(this.pdfDocument));
        this.updatePDFView();
    }

    private void updatePDFView() {
        if (this.pdfDocument != null) {
            this.pdfViewPanel
                    .setOverlayImages(PDFPageRenderer.getTextHighlightingOverlayFromDocument(this.pdfDocument));
        }
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
        int posX = (int) ((dimension.getWidth() * relativeX) - (double) width / 2);
        int posY = (int) ((dimension.getHeight() * relativeY) - (double) height / 2);

        return new Rectangle(posX, posY, width, height);
    }
}
