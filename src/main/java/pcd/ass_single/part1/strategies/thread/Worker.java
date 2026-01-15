package pcd.ass_single.part1.strategies.thread;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import pcd.ass_single.part1.SearchModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Worker extends Thread {
    final private Monitor cell;
    final private List<File>  files;
    final private int start;
    final private int end;
    final private String searchedWord;
    final private SearchModel model;

    public Worker(Monitor cell, int start, int end, List<File> files, String word, SearchModel model) {
        super("getter");
        this.cell = cell;
        this.files = files;
        this.start = start;
        this.end = end;
        this.searchedWord = word;
        this.model = model;
    }

    public void run(){
        int count = 0;
        for (int i = start; i < end; ++i) {
                try {
                    model.checkState();
                    if (containsWord(files.get(i), searchedWord)) {
                        count += 1;
                        model.incCountPdfFilesWithWord();
                    }
                } catch (InterruptedException e) {
                    System.out.println("STOP");
                    break;
                }catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
        cell.updateFoundFiles(end - start, count);
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
}
