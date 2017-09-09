package com.noxag.newnox.pot.userinterface.pdfmodule;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PDFPageDrawer {
    private final static int SCALING_FACTOR = 2;

    public static Image getPageFromPDFAsImage(PDDocument doc, int pageIndex) {
        PDFRenderer renderer = new PDFRenderer(doc);
        doc.getNumberOfPages();
        try {
            return renderer.renderImage(pageIndex, SCALING_FACTOR);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Image> getPagesFromPDFAsImage(PDDocument doc, int pageIndex, int pageIndexOffset) {
        List<Image> pdfPages = new ArrayList<>();
        for (int i = pageIndex; i < (pageIndex + pageIndexOffset); i++) {
            pdfPages.add(PDFPageDrawer.getPageFromPDFAsImage(doc, i));
        }
        return pdfPages;
    }

    public static List<Image> getAllPagesFromPDFAsImage(PDDocument doc) {
        return getPagesFromPDFAsImage(doc, 0, doc.getNumberOfPages() - 1);
    }
}
