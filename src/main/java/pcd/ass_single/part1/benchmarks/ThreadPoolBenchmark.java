package pcd.ass_single.part1.benchmarks;

import pcd.ass_single.part1.SearchModel;
import pcd.ass_single.part1.strategies.BasicSearch;
import pcd.ass_single.part1.strategies.PdfWordSearcher;
import pcd.ass_single.part1.strategies.async_event.VertxAsyncSearcher;
import pcd.ass_single.part1.strategies.reactive_prog.RxJavaSearcher;
import pcd.ass_single.part1.strategies.task_based.ForkJoinSearcher;
import pcd.ass_single.part1.strategies.thread.ThreadPoolSearch;
import pcd.ass_single.part1.strategies.virtual_threads.VirtualThreadSearcher;

import java.io.File;
import java.util.*;

import static pcd.ass_single.part1.benchmarks.BenchmarkCommon.collectPdfFiles;
import static pcd.ass_single.part1.benchmarks.BenchmarkCommon.log;

public class ThreadPoolBenchmark {
    static SearchModel placeholderModel = new SearchModel(new BasicSearch());
    static String word = "goku";
    static final Integer numExecutions = 7;

    public static void main(String[] args) throws Exception {

        // SETUP
        PdfWordSearcher singleThreadedSearcher = new BasicSearch();
        List<Long> singleThreadedTimes = new ArrayList<>();
        PdfWordSearcher threadPoolSearcher = new ThreadPoolSearch();
        List<Long> threadPoolTimes = new ArrayList<>();

        File pdfsDir = new File("pdfs");
        List<String> testFolders = Arrays.stream(Objects.requireNonNull(pdfsDir.listFiles()))
                .filter(File::isDirectory)
                .map(f -> "pdfs/" + f.getName())
                .toList();

        Map<String, List<File>> fileLists = new HashMap<>();

        for (String folder : testFolders) {
            fileLists.put(folder, collectPdfFiles(folder));
//            fileLists.get(folder).forEach(System.out::println);
        }



        individualBenchmarker(testFolders, fileLists, singleThreadedSearcher, singleThreadedTimes);
        individualBenchmarker(testFolders, fileLists, threadPoolSearcher, threadPoolTimes);

        log("THREADS-based approach benchmark:");
        for (int i = 0; i < singleThreadedTimes.size(); i++) {
            double baselineMs = singleThreadedTimes.get(i) / 1_000_000.0;
            double parallelMs = threadPoolTimes.get(i) / 1_000_000.0;
            double speedup = (double)singleThreadedTimes.get(i) / threadPoolTimes.get(i);

            log(testFolders.get(i));
            log(String.format("  Sequential:  %8.2f ms", baselineMs));
            log(String.format("  Thread Pool: %8.2f ms", parallelMs));
            log(String.format("  Speedup:     %8.2fx", speedup));
            log("---------------------------------------\n");
        }

        log("-----------------------------");

    }

    private static void individualBenchmarker(List<String> testFolders, Map<String, List<File>> fileLists, PdfWordSearcher scraper, List<Long> times) throws Exception {
        for (String currFolder : testFolders) {
            long sum = 0;
            for (int i = 0; i < numExecutions; i++) {
                long start = System.nanoTime();
                scraper.extractText(fileLists.get(currFolder), word, placeholderModel);
                long end = System.nanoTime();
                sum += end - start;
            }
            times.add(sum / numExecutions);
        }

    }

}
