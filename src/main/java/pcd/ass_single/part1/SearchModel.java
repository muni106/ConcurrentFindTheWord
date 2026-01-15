package pcd.ass_single.part1;

import pcd.ass_single.part1.strategies.PdfWordSearcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchModel {

    private final List<ModelObserver> observers;
    private volatile int countFiles;
    private volatile int countPdfFiles;
    private volatile int countPdfFilesWithWord;
    private List<File> pdfs;
    private final PdfWordSearcher searcher;

    // control state
    private volatile boolean stopped = false;
    private volatile boolean suspended = false;
    private final Object suspendLock = new Object();

    public SearchModel(PdfWordSearcher searcher){
        this.searcher = searcher;
        countFiles = 0;
        countPdfFiles = 0;
        countPdfFilesWithWord = 0;
        observers = new ArrayList<>();
    }

    public void startFromScratch(String directoryPath, String searchWord) {
        stopped = false;
        suspended = false;
        countFiles = 0;
        countPdfFiles = 0;
        countPdfFilesWithWord = 0;
        notifyObservers();
        pdfs = collectPdfFiles(directoryPath);
        searchPdfsWithWord(searchWord);
    }

    private void searchPdfsWithWord(String searchedWord) {
        try {
            searcher.extractText(pdfs, searchedWord, this);
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

    public void setCountFiles(int countFiles) {
        this.countFiles = countFiles;
    }

    public void setCountPdfFiles(int countPdfFiles) {
        this.countPdfFiles = countPdfFiles;
    }

    public void stop() {
        stopped = true;
        resume();
    }

    public void suspend() {
        suspended = true;
    }

    public void resume() {
        synchronized (suspendLock) {
            suspended = false;
            suspendLock.notifyAll();
        }
    }

    // Workers call this to check state
    public void checkState() throws InterruptedException {
        if (stopped) {
            throw new InterruptedException("Search stopped");
        }
        synchronized (suspendLock) {
            while (suspended && !stopped) {
                suspendLock.wait();
            }
        }
    }

    private void notifyObservers() {
        for (ModelObserver obs: observers) {
            obs.modelUpdated(this);
        }
    }
}
