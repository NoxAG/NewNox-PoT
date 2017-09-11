package com.noxag.newnox.pot.userinterface.pdfmodule;

import java.util.function.Function;

import org.apache.pdfbox.pdmodel.PDDocument;

public class PDFAnalyzer {
    Function<PDDocument, String> algorithm;

    public PDFAnalyzer(Function<PDDocument, String>... consumers) {
        algorithm = consumers[0];
    }

    public void analyze(PDDocument pdfDocument) {
        algorithm.apply(pdfDocument);
    }

    public static void main() {
        new PDFAnalyzer(PDFAnalyzer::highlight, PDFAnalyzer::wordStatistic);
    }

    public static String highlight(PDDocument pdfDocument) {
        return null;
    }

    public static String wordStatistic(PDDocument pdfDocument) {
        return null;
    }
}
