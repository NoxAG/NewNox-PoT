package com.noxag.newnox.pot.userinterface.pdfmodule;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;

public class TextHighlightingDrawer extends PageDrawer {
    private static final Logger LOGGER = Logger.getLogger(TextHighlightingDrawer.class.getName());

    TextHighlightingDrawer(PageDrawerParameters parameters) throws IOException {
        super(parameters);
    }

    @Override
    public void processPage(PDPage page) throws IOException {
        // Don't process page content
        // This way the page is left blank and the TextHighlightingDrawer
        // becomes much faster
    }

    /**
     * Custom annotation rendering.
     */
    @Override
    public void showAnnotation(PDAnnotation annotation) throws IOException {
        // TODO: Add support for more SUB_Types !
        if (annotation instanceof PDAnnotationTextMarkup
                && annotation.getSubtype().equals(PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT)) {

            Graphics2D graphics = getGraphics();
            Color color = graphics.getColor();
            Composite composite = graphics.getComposite();
            Shape clip = graphics.getClip();

            drawAnnotation(graphics, annotation);

            // restore
            graphics.setColor(color);
            graphics.setClip(clip);
            graphics.setComposite(composite);
        }
    }

    private void drawAnnotation(Graphics2D graphics, PDAnnotation annotation) {
        PDRectangle annotationRect = annotation.getRectangle();
        Shape bbox = new Rectangle2D.Float(annotationRect.getLowerLeftX(), annotationRect.getLowerLeftY(),
                annotationRect.getWidth(), annotationRect.getHeight());

        graphics.setClip(graphics.getDeviceConfiguration().getBounds());
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                ((PDAnnotationTextMarkup) annotation).getConstantOpacity()));

        try {
            graphics.setColor(new Color(annotation.getColor().toRGB()));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Color of Annotation could not be drawn", e);
        }

        graphics.fill(bbox);
    }
}