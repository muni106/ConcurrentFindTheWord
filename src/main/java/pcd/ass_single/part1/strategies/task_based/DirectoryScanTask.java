package pcd.ass_single.part1.strategies.task_based;

import pcd.ass_single.part1.SearchModel;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class DirectoryScanTask extends RecursiveTask<Integer> {
    private final DirectoryTree directory;
    private final String searchedWord;
    private final FileCounter fc;
    private SearchModel model;

    public DirectoryScanTask(FileCounter fc, DirectoryTree directory, String searchedWord, SearchModel model) {
        super();
        this.fc = fc;
        this.directory = directory;
        this.searchedWord = searchedWord;
        this.model = model;
    }

    @Override
    protected Integer compute() {
        int count = 0;
        List<RecursiveTask<Integer>> forks = new LinkedList<>();
        for (DirectoryTree subDirectory : directory.getSubDirectories()) {
            DirectoryScanTask task = new DirectoryScanTask(fc, subDirectory, searchedWord, model);
            forks.add(task);
            task.fork();
        }

        for (File pdf: directory.getPdfs()) {
            WordSearchTask task = new WordSearchTask(fc, pdf, searchedWord, model);
            forks.add(task);
            task.fork();
        }

        for (RecursiveTask<Integer> task : forks) {
            count = count + task.join();
        }

        return count;
    }
}
