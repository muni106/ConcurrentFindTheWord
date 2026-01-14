package pcd.ass_single.part1.benchmarks;

import pcd.ass_single.part1.SearchModel;
import pcd.ass_single.part1.strategies.BasicSearch;
import pcd.ass_single.part1.strategies.PdfWordSearcher;
import pcd.ass_single.part1.strategies.task_based.ForkJoinSearcher;
import pcd.ass_single.part1.strategies.thread.ThreadPoolSearch;

import java.io.File;
import java.util.*;

import static pcd.ass_single.part1.benchmarks.BenchmarkCommon.collectPdfFiles;
import static pcd.ass_single.part1.benchmarks.BenchmarkCommon.log;

public class ForkJoinBenchmark {
    static SearchModel placeholderModel = new SearchModel(new BasicSearch());
    static String word = "goku";
    static final Integer numExecutions = 7;

    public static void main(String[] args) throws Exception {

        // SETUP
        PdfWordSearcher singleThreadedSearcher = new BasicSearch();
        List<Long> singleThreadedTimes = new ArrayList<>();
        PdfWordSearcher forkJoinSearcher = new ForkJoinSearcher();
        List<Long> forkJoinTimes = new ArrayList<>();

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

        warmup(testFolders, fileLists, singleThreadedSearcher);
        individualBenchmarker(testFolders, fileLists, singleThreadedSearcher, singleThreadedTimes);

        warmup(testFolders, fileLists, forkJoinSearcher);
        individualBenchmarker(testFolders, fileLists, forkJoinSearcher, forkJoinTimes);

        log("FORKJOIN-based approach benchmark:");
        for (int i = 0; i < singleThreadedTimes.size(); i++) {
            double singleThreadMs = singleThreadedTimes.get(i) / 1_000_000.0;
            double forkJoinMs = forkJoinTimes.get(i) / 1_000_000.0;
            double speedup = (double)singleThreadedTimes.get(i) / forkJoinTimes.get(i);

            log(testFolders.get(i));
            log(" Avg Sequential time: " + singleThreadMs + " ms");
            log(" Avg Thread Pool time: " + forkJoinMs + " ms");
            log(" Speedup: " + speedup);
            log("---------------------------------------");
        }

    }

    private static void individualBenchmarker(List<String> testFolders, Map<String, List<File>> fileLists, PdfWordSearcher scraper, List<Long> times) throws Exception {
        log("real");
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

    // to reduce java's JIT
    private static void warmup(List<String> testFolders,  Map<String, List<File>> fileLists, PdfWordSearcher scraper) throws Exception {
        log("warmup");
        for (String currFolder : testFolders) {
            if (fileLists.get(currFolder).size() < 1000) {
                scraper.extractText(fileLists.get(currFolder), word, placeholderModel);
            }
        }
    }

}
