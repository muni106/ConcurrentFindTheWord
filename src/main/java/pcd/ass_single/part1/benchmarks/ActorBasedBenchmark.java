package pcd.ass_single.part1.benchmarks;

import pcd.ass_single.part1.SearchModel;
import pcd.ass_single.part1.strategies.BasicSearch;
import pcd.ass_single.part1.strategies.PdfWordSearcher;
import pcd.ass_single.part1.strategies.actors.ActorBasedSearcher;
import pcd.ass_single.part1.strategies.async_event.VertxAsyncSearcher;
import pcd.ass_single.part1.strategies.reactive_prog.RxJavaSearcher;
import pcd.ass_single.part1.strategies.task_based.ForkJoinSearcher;
import pcd.ass_single.part1.strategies.thread.ThreadPoolSearch;
import pcd.ass_single.part1.strategies.virtual_threads.VirtualThreadSearcher;

import java.io.File;
import java.util.*;

import static pcd.ass_single.part1.benchmarks.BenchmarkCommon.collectPdfFiles;
import static pcd.ass_single.part1.benchmarks.BenchmarkCommon.log;

public class ActorBasedBenchmark {
    static SearchModel placeholderModel = new SearchModel(new BasicSearch());
    static String word = "goku";
    public static void main(String[] args) throws Exception {

        // SETUP
        PdfWordSearcher singleThreadedSearcher = new BasicSearch();
        List<Long> singleThreadedTimes = new ArrayList<>();
        PdfWordSearcher actorBasedSearcher = new ActorBasedSearcher();
        List<Long> actorBasedTimes = new ArrayList<>();

        File pdfsDir = new File("pdfs");
        List<String> testFolders = Arrays.stream(Objects.requireNonNull(pdfsDir.listFiles()))
                .filter(File::isDirectory)
                .map(f -> "pdfs/" + f.getName())
                .toList();

        Map<String, List<File>> fileLists = new HashMap<>();

        for (String folder : testFolders) {
            fileLists.put(folder, collectPdfFiles(folder));
            fileLists.get(folder).forEach(System.out::println);
        }

        final Integer numExecutions = 7;

        individualBenchmarker(testFolders, fileLists, singleThreadedSearcher, singleThreadedTimes);
        individualBenchmarker(testFolders, fileLists, actorBasedSearcher, actorBasedTimes);
        log("ACTOR-based approach benchmark:");
        for (int i = 0; i < singleThreadedTimes.size(); i++) {
            log(testFolders.get(i));
            log("speedup = " + (double)(singleThreadedTimes.get(i) / actorBasedTimes.get(i)));
        }
        log("-----------------------------");
    }

    private static synchronized void individualBenchmarker(List<String> testFolders, Map<String, List<File>> fileLists, PdfWordSearcher scraper, List<Long> times) throws Exception {
        for (String currFolder : testFolders) {
            long start = System.nanoTime();
            scraper.extractText(fileLists.get(currFolder), word, placeholderModel);
            long end = System.nanoTime();
            times.add(end - start);
        }
    }


}
