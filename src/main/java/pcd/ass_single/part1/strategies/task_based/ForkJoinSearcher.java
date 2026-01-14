package pcd.ass_single.part1.strategies.task_based;

import pcd.ass_single.part1.strategies.PdfWordSearcher;
import pcd.ass_single.part1.SearchModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ForkJoinSearcher implements PdfWordSearcher {

    @Override
    public void extractText(List<File> files, String word, SearchModel model) throws IOException {
        long startTime = System.currentTimeMillis();
        int Ncpu = Runtime.getRuntime().availableProcessors();
        // A system with Ncpu processors usually achieves optimum utilization with a thread pool of Ncpu + 1 threads
        int Nthreads = Ncpu + 1;
        Integer count = 0;

        if (files != null && !files.isEmpty()) {
            // next line is a little bit ugly
            DirectoryTree dir = DirectoryTree.fromDirectory(files.getFirst().getParentFile());
            FileCounter fc = new FileCounter(model);
            count = fc.countFilesInParallel(dir, word);
//            System.out.println("The number of files with the word " + word + " is: " + count);
        }else {
            System.err.println("No files found");
        }
    }

}


