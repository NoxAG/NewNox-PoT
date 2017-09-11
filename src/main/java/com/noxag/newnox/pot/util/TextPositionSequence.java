package com.noxag.newnox.pot.util;

import java.util.List;

import org.apache.pdfbox.text.TextPosition;

public class TextPositionSequence implements CharSequence {

    final List<TextPosition> textPositions;
    final int start, end;

    public TextPositionSequence(List<TextPosition> textPositions) {
        this(textPositions, 0, textPositions.size());
    }

    public TextPositionSequence(List<TextPosition> textPositions, int start, int end) {
        this.textPositions = textPositions;
        this.start = start;
        this.end = end;
    }

    @Override
    public int length() {
        return end - start;
    }

    @Override
    public char charAt(int index) {
        TextPosition textPosition = textPositionAt(index);
        String text = textPosition.getUnicode();
        return text.charAt(0);
    }

    @Override
    public TextPositionSequence subSequence(int start, int end) {
        return new TextPositionSequence(textPositions, this.start + start, this.start + end);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(length());
        for (int i = 0; i < length(); i++) {
            builder.append(charAt(i));
        }
        return builder.toString();
    }

    public TextPosition textPositionAt(int index) {
        return textPositions.get(start + index);
    }

    public float getX() {
        return textPositions.get(start).getXDirAdj();
    }

    public float getY() {
        return textPositions.get(start).getPageHeight() - textPositions.get(start).getYDirAdj();
    }

    public float getWidth() {
        TextPosition first = textPositions.get(start);
        TextPosition last = textPositions.get(end);
        return last.getWidthDirAdj() + last.getXDirAdj() - first.getXDirAdj();
    }

    public float getHeight() {

        TextPosition first = textPositions.get(start);
        return first.getHeightDir();
    }

}