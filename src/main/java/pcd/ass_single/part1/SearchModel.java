package pcd.ass_single.part1;

import pcd.ass_single.part1.strategies.PdfWordSearcher;
import pcd.ass_single.part1.strategies.async_event.VertxAsyncSearcher;
import pcd.ass_single.part1.strategies.reactive_prog.RxJavaSearcher;
import pcd.ass_single.part1.strategies.task_based.ForkJoinSearcher;
import pcd.ass_single.part1.strategies.thread.ThreadPoolSearch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchModel {

    private List<ModelObserver> observers;
    private volatile int countFiles;
    private volatile int countPdfFiles;
    private volatile int countPdfFilesWithWord;
    private List<File> pdfs;

    public SearchModel(){
        countFiles = 0;
        countPdfFiles = 0;
        countPdfFilesWithWord = 0;
        observers = new ArrayList<>();
    }

    public void startFromScratch(String directoryPath, String searchWord) {
        countFiles = 0;
        countPdfFiles = 0;
        countPdfFilesWithWord = 0;
        notifyObservers();
        pdfs = collectPdfFiles(directoryPath);
        scrapePdfsWithWord(searchWord);
    }

    private void scrapePdfsWithWord(String searchedWord) {
        PdfWordSearcher textScraper = new VertxAsyncSearcher();
        try {
            textScraper.extractText(pdfs, searchedWord, this);
        } catch ( Exception e ) {
            System.err.println(e.getMessage());
        }

        notifyObservers();
    }

    private List<File> collectPdfFiles(String directoryPath) {
        System.out.println("Extracting files from directory: " + directoryPath);
        File directory = new File(directoryPath);

        File[] files = directory.listFiles();

        ArrayList<File> pdfs = new ArrayList<>();

        if (files != null) {
            for ( File file : files ) {
                incCountFiles();
                if (file.isDirectory()) {
                    pdfs.addAll(collectPdfFiles(file.getAbsolutePath()));
                } else if (file.isFile() && file.getName().endsWith(".pdf")) {
                    incCountPdfFiles();
                    pdfs.add(file);
                }
            }
        }
        return pdfs;
    }

    public void addObserver(ModelObserver obs) {
        observers.add(obs);
    }

    public int getCountFiles() {
        return countFiles;
    }

    public int getCountPdfFiles() {
        return countPdfFiles;
    }

    public int getCountPdfFilesWithWord() {
        return countPdfFilesWithWord;
    }

    public void setCountPdfFilesWithWord(int countPdfFilesWithWord) {
        this.countPdfFilesWithWord = countPdfFilesWithWord;
        notifyObservers();
    }


    public synchronized void incCountFiles() {
        this.countFiles += 1;
        notifyObservers();
    }

    public synchronized void incCountPdfFiles() {
        this.countPdfFiles += 1;
        notifyObservers();
    }

    public synchronized void incCountPdfFilesWithWord() {
        this.countPdfFilesWithWord += 1;
        notifyObservers();
    }


    private void notifyObservers() {
        for (ModelObserver obs: observers) {
            obs.modelUpdated(this);
        }
    }
}
