package pcd.ass_single.part1.strategies;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import pcd.ass_single.part1.SearchModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BasicSearch implements PdfWordSearcher {

    @Override
    public void extractText(List<File> files, String word, SearchModel model) throws IOException {

        long startTime = System.currentTimeMillis();

        int count = 0;


        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".pdf")) {
                    if (containsWord(file, word)) {
                        model.incCountPdfFilesWithWord();
                        count += 1;
                    }
                }
            }
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Total time: " + (endTime - startTime) + " ms");

    }

    private static boolean containsWord(File pdf, String word) throws IOException {
        PDDocument document = PDDocument.load(pdf);

        AccessPermission ap = document.getCurrentAccessPermission();
        if (!ap.canExtractContent()) {
            throw new IOException("You do not have permission to extract text");
        }
        PDFTextStripper stripper = new PDFTextStripper();

        String text = stripper.getText(document);


        if (text.contains(word)) {
            document.close();
            return true;
        }
        document.close();
        return false;
    }


    private static void usage() {
        System.err.println("Usage: java " + BasicSearch.class.getName() + " <directory> <word>");
        System.exit(-1);
    }
}
