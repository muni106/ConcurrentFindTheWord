package pcd.ass_single.part1.strategies.task_based;

import pcd.ass_single.part1.SearchModel;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.RecursiveTask;

public class WordSearchTask extends RecursiveTask<Integer> {
    private final File pdf;
    private final String searchedWord;
    private final FileCounter fc;
    private SearchModel model;

    public WordSearchTask(FileCounter fc, File pdf, String searchedWord, SearchModel model) {
        super();
        this.pdf = pdf;
        this.searchedWord = searchedWord;
        this.fc = fc;
        this.model = model;
    }

    @Override
    protected Integer compute() {
        try {
            int compRes = fc.containsWord(pdf, searchedWord);
            if (compRes == 1) {
                model.incCountPdfFilesWithWord();
            }
            return compRes;
        } catch (IOException e) {
            System.err.println("Error occurred while searching for in the file: " + pdf.getName());
            return 0;
        }
    }
}
