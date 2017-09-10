package com.noxag.newnox.pot.userinterface.pdfmodule;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class PDFView extends JPanel {

    private static final long serialVersionUID = 8613272018241811244L;

    // page gap in percent of page height
    private static final double PAGE_GAP_FACTOR = 0.05;

    private transient List<Image> originalScalePDFImages;
    private transient List<Image> scaledPDFImages;

    private double scaleFactor;

    public PDFView() {
        this.setBackground(Color.LIGHT_GRAY);
        originalScalePDFImages = new ArrayList<>();
    }

    public void defaultScale() {
        this.setScaleFactor(1.0);
        this.rescale();
    }

    public void rescale() {
        if (originalScalePDFImages != null) {
            scaledPDFImages = new ArrayList<>();
            int newWidth = (int) (this.getParent().getWidth() * this.getScaleFactor());
            int newHeight = 0;
            int pageHeight = 0;
            for (Image originalScalePDFImage : originalScalePDFImages) {
                if (originalScalePDFImage == null) {
                    continue;
                }
                Image scaledPDFImage = originalScalePDFImage.getScaledInstance(newWidth, -1, Image.SCALE_FAST);
                pageHeight = scaledPDFImage.getHeight(null);
                newHeight += pageHeight + pageHeight * PAGE_GAP_FACTOR;
                scaledPDFImages.add(scaledPDFImage);
            }
            // last page doesnt need a gap: reverse
            newHeight -= pageHeight * PAGE_GAP_FACTOR;
            this.setPreferredSize(new Dimension(newWidth, newHeight));
            this.repaint();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (scaledPDFImages != null) {
            int posY = 0;
            int posX = 0;
            for (Image scaledPDFImage : scaledPDFImages) {
                int pageHeight = scaledPDFImage.getHeight(null);
                int scrollPaneWidth = (int) (this.getParent().getWidth());
                posX = (int) (scrollPaneWidth / 2 - (scrollPaneWidth * this.getScaleFactor()) / 2);

                g.drawImage(scaledPDFImage, posX, posY, this);
                posY += (int) (pageHeight + pageHeight * PAGE_GAP_FACTOR);
            }
        }
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public List<Image> getPDFImage() {
        return originalScalePDFImages;
    }

    public void setPDFImage(List<Image> pictures) {
        this.originalScalePDFImages = pictures;
    }

    public void addPDFImage(Image picture) {
        this.originalScalePDFImages.add(picture);
    }
}
