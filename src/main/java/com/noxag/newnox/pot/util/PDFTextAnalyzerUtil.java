package com.noxag.newnox.pot.util;

import java.util.ArrayList;
import java.util.List;

import com.noxag.newnox.pot.util.data.PDFLine;
import com.noxag.newnox.pot.util.data.PDFPage;
import com.noxag.newnox.pot.util.data.TextPositionSequence;

public class PDFTextAnalyzerUtil {

    public static List<TextPositionSequence> reduceToWords(List<PDFPage> pages) {
        return pages.stream().reduce(new ArrayList<TextPositionSequence>(), (textPositionList, page) -> {
            textPositionList.addAll(page.getWords());
            return textPositionList;
        }, (list1, list2) -> {
            list1.addAll(list2);
            return list1;
        });
    }

    public static List<PDFLine> reduceToLines(List<PDFPage> pages) {
        return pages.stream().reduce(new ArrayList<PDFLine>(), (pdfLineList, page) -> {
            pdfLineList.addAll(page.getLines());
            return pdfLineList;
        }, (list1, list2) -> {
            list1.addAll(list2);
            return list1;
        });
    }

    public static List<TextPositionSequence> reduceToTextPositions(List<PDFLine> lines) {
        return lines.stream().reduce(new ArrayList<TextPositionSequence>(), (textPositionList, line) -> {
            textPositionList.add(line.getTextPositionSequence());
            return textPositionList;
        }, (list1, list2) -> {
            list1.addAll(list2);
            return list1;
        });
    }
}
