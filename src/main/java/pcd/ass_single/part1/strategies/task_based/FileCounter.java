package pcd.ass_single.part1.strategies.task_based;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import pcd.ass_single.part1.SearchModel;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

public class FileCounter {
    private final ForkJoinPool forkJoinPool = new ForkJoinPool();
    private SearchModel model;

    public FileCounter(SearchModel model) {
        this.model = model;
    }

    public int containsWord(File pdf, String word) throws IOException {
        PDDocument document = PDDocument.load(pdf);

        AccessPermission ap = document.getCurrentAccessPermission();
        if (!ap.canExtractContent()) {
            throw new IOException("You do not have permission to extract text");
        }
        PDFTextStripper stripper = new PDFTextStripper();

        String text = stripper.getText(document);

        if (text.contains(word)) {
            document.close();
            return 1;
        }
        document.close();
        return 0;
    }

    public Integer countFilesInParallel(DirectoryTree dir, String searchedWord) throws IOException {
        try {

            return forkJoinPool.invoke(new DirectoryScanTask(this, dir, searchedWord, model));
        } finally {
            forkJoinPool.shutdown();
        }
    }
}
