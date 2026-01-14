package pcd.ass_single.part1.benchmarks;

import pcd.ass_single.part1.SearchModel;
import pcd.ass_single.part1.strategies.BasicSearch;
import pcd.ass_single.part1.strategies.PdfWordSearcher;
import pcd.ass_single.part1.strategies.virtual_threads.VirtualThreadSearcher;

import java.io.File;
import java.util.*;

import static pcd.ass_single.part1.benchmarks.BenchmarkCommon.collectPdfFiles;
import static pcd.ass_single.part1.benchmarks.BenchmarkCommon.log;

public class VirtualThreadBenchmark {
    static SearchModel placeholderModel = new SearchModel(new BasicSearch());
    static String word = "goku";
    static final Integer numExecutions = 7;

    public static void main(String[] args) throws Exception {

        // SETUP
        PdfWordSearcher singleThreadedSearcher = new BasicSearch();
        List<Long> singleThreadedTimes = new ArrayList<>();
        PdfWordSearcher vistualThreadSearcher = new VirtualThreadSearcher();
        List<Long> vistualThreadTimes = new ArrayList<>();

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

        warmup(testFolders, fileLists, vistualThreadSearcher);
        individualBenchmarker(testFolders, fileLists, vistualThreadSearcher, vistualThreadTimes);

        log("VIRTUAL THREAD approach benchmark:");
        for (int i = 0; i < singleThreadedTimes.size(); i++) {
            double singleThreadMs = singleThreadedTimes.get(i) / 1_000_000.0;
            double vistualThreadMs = vistualThreadTimes.get(i) / 1_000_000.0;
            double speedup = (double)singleThreadedTimes.get(i) / vistualThreadTimes.get(i);

            log(testFolders.get(i));
            log(" Avg Sequential time: " + singleThreadMs + " ms");
            log(" Avg Thread Pool time: " + vistualThreadMs + " ms");
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
