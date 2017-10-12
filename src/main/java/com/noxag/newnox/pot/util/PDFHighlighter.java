package com.noxag.newnox.pot.util;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;

import com.noxag.newnox.pot.util.data.TextFinding;
import com.noxag.newnox.pot.util.data.TextPositionSequence;

public class PDFHighlighter {
    private static final Logger LOGGER = Logger.getLogger(PDFHighlighter.class.getName());

    public static void highlight(PDDocument pdfDoc, List<TextFinding> textFindings) throws IOException {
        for (TextFinding finding : textFindings) {
            addTextMarkupAnnotation(pdfDoc, finding.getTextPositionSequence());
        }
    }

    /**
     * Adds an Annotation to the document to highlights the given text sequence
     * 
     * @param doc
     * @param annotationPosition
     * @throws IOException
     * 
     */
    public static void addTextMarkupAnnotation(PDDocument doc, TextPositionSequence annotationPosition)
            throws IOException {
        addTextMarkupAnnotation(doc, annotationPosition, new PDColor(new float[] { 1, 0, 0 }, PDDeviceRGB.INSTANCE),
                PDAnnotationTextMarkup.SUB_TYPE_STRIKEOUT);
    }

    public static void addTextMarkupAnnotation(PDDocument doc, TextPositionSequence annotationPosition, PDColor color,
            String subType) throws IOException {
        doc.getPage(annotationPosition.getPageNum() - 1).getAnnotations()
                .add(generateTextMarkupAnnotation(annotationPosition, color, subType));
    }

    private static PDAnnotation generateTextMarkupAnnotation(TextPositionSequence annotationPosition, PDColor color,
            String subType) {
        PDAnnotationTextMarkup txtMark = new PDAnnotationTextMarkup(subType);
        txtMark.setColor(color);
        txtMark.setConstantOpacity(1f);
        txtMark.setRectangle(new PDRectangle(annotationPosition.getX(), annotationPosition.getY(),
                annotationPosition.getWidth(), annotationPosition.getHeight()));
        return txtMark;
    }

    public static void clearDocumentFromAnnotations(PDDocument doc) {
        try {
            for (int pageNum = 1; pageNum < doc.getNumberOfPages(); pageNum++) {
                List<PDAnnotation> pageAnnotations = doc.getPage(pageNum - 1).getAnnotations();
                pageAnnotations.clear();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "PDF could not acces annotations", e);
        }
    }

}
