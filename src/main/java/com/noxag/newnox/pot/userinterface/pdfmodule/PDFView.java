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

    private transient List<Image> originalScaleTextHighlightOverlayImages;
    private transient List<Image> scaledTextHighlightOverlayImages;

    private double scaleFactor;

    private boolean showTextHighlight = true;
    private boolean showComments = false;

    public PDFView() {
        this.setBackground(Color.LIGHT_GRAY);
        originalScalePDFImages = new ArrayList<>();
    }

    public void defaultScale() {
        this.setScaleFactor(1.0);
        this.rescale();
    }

    public void rescale() {
        if (originalScaleTextHighlightOverlayImages != null) {
            scaledTextHighlightOverlayImages = new ArrayList<>();
            int newWidth = (int) (this.getParent().getWidth() * this.getScaleFactor());
            int newHeight = 0;
            int pageHeight = 0;
            for (Image originalScaleOverlayImage : originalScaleTextHighlightOverlayImages) {
                if (originalScaleOverlayImage == null) {
                    continue;
                }
                Image scaledPDFImage = originalScaleOverlayImage.getScaledInstance(newWidth, -1, Image.SCALE_FAST);
                pageHeight = scaledPDFImage.getHeight(null);
                newHeight += pageHeight + pageHeight * PAGE_GAP_FACTOR;

                scaledTextHighlightOverlayImages.add(scaledPDFImage);
            }
            // last page doesn't need a gap: undo last step
            newHeight -= pageHeight * PAGE_GAP_FACTOR;
            this.setPreferredSize(new Dimension(newWidth, newHeight));
            this.repaint();
        }
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
            // last page doesn't need a gap: undo last step
            newHeight -= pageHeight * PAGE_GAP_FACTOR;
            this.setPreferredSize(new Dimension(newWidth, newHeight));
            this.repaint();
        }

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        paintBackground(g, scaledPDFImages);
        if (this.showTextHighlight) {
            paintImages(g, scaledTextHighlightOverlayImages);
        }
        paintImages(g, scaledPDFImages);
    }

    private void paintBackground(Graphics g, List<Image> scaledPDFImages) {
        g.setColor(new Color(255, 255, 255, 255));
        if (scaledPDFImages != null) {
            int posY = 0;
            int posX = 0;
            for (Image image : scaledPDFImages) {
                int pageHeight = image.getHeight(null);
                int pageWidth = image.getWidth(null);
                int scrollPaneWidth = (int) (this.getParent().getWidth());
                posX = (int) (scrollPaneWidth / 2 - (scrollPaneWidth * this.getScaleFactor()) / 2);

                g.fillRect(posX, posY, pageWidth, pageHeight);
                posY += (int) (pageHeight + pageHeight * PAGE_GAP_FACTOR);
            }
        }
    }

    private void paintImages(Graphics g, List<Image> images) {
        if (images != null) {
            int posY = 0;
            int posX = 0;
            for (Image image : images) {
                int pageHeight = image.getHeight(null);
                int scrollPaneWidth = (int) (this.getParent().getWidth());
                posX = (int) (scrollPaneWidth / 2 - (scrollPaneWidth * this.getScaleFactor()) / 2);

                g.drawImage(image, posX, posY, this);
                posY += (int) (pageHeight + pageHeight * PAGE_GAP_FACTOR);
            }
        }
    }

    public boolean isTextHighlightShowing() {
        return this.showTextHighlight;
    }

    public boolean isCommentOverlayShowing() {
        return this.showComments;
    }

    public void showCommentOverlay(boolean b) {
        this.showComments = b;
    }

    public void showTextHighlightOverlay(boolean b) {
        this.showTextHighlight = b;
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

    public List<Image> getOverlayImages() {
        return originalScaleTextHighlightOverlayImages;
    }

    public void setOverlayImages(List<Image> overlayImages) {
        this.originalScaleTextHighlightOverlayImages = overlayImages;
    }

}
