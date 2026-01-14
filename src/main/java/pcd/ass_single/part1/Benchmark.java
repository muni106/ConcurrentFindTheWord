package pcd.ass_single.part1;

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
import java.util.stream.Collectors;

public class Benchmark {
    static SearchModel placeholderModel = new SearchModel(new BasicSearch());
    static String word = "goku";
    public static void main(String[] args) throws Exception {

        // SETUP
        PdfWordSearcher singleThreadedSearcher = new BasicSearch();
        List<Long> singleThreadedTimes = new ArrayList<>();
        PdfWordSearcher threadPoolSearcher = new ThreadPoolSearch();
        List<Long> threadPoolTimes = new ArrayList<>();
        PdfWordSearcher virtualThreadPoolSearcher = new VirtualThreadSearcher();
        List<Long> virtualThreadPoolTimes = new ArrayList<>();
        PdfWordSearcher forkJoinSearcher = new ForkJoinSearcher();
        List<Long> forkJoinTimes = new ArrayList<>();
        PdfWordSearcher reactiveSearcher = new RxJavaSearcher();
        List<Long> reactiveTimes = new ArrayList<>();
        PdfWordSearcher asyncEventBasedSearcher = new VertxAsyncSearcher();
        List<Long> asyncEventBasedTimes = new ArrayList<>();
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
        individualBenchmarker(testFolders, fileLists, threadPoolSearcher, threadPoolTimes);

        for (int i = 0; i < singleThreadedTimes.size(); i++) {
            log(testFolders.get(i));
            log("speedup = " + (singleThreadedTimes.get(i) / threadPoolTimes.get(i)));
        }




    }

    private static synchronized void individualBenchmarker(List<String> testFolders, Map<String, List<File>> fileLists, PdfWordSearcher scraper, List<Long> times) throws Exception {
        for (String currFolder : testFolders) {
            long start = System.currentTimeMillis();
            scraper.extractText(fileLists.get(currFolder), word, placeholderModel);
            long end = System.currentTimeMillis();
            times.add(end - start);
        }
    }

    private static void log(String msg) {
        System.out.println(msg);
    }

    private static List<File> collectPdfFiles(String directoryPath) {
        System.out.println("Extracting files from directory: " + directoryPath);
        File directory = new File(directoryPath);

        File[] files = directory.listFiles();

        ArrayList<File> pdfs = new ArrayList<>();

        if (files != null) {
            for ( File file : files ) {
                if (file.isDirectory()) {
                    pdfs.addAll(collectPdfFiles(file.getAbsolutePath()));
                } else if (file.isFile() && file.getName().endsWith(".pdf")) {
                    pdfs.add(file);
                }
            }
        }
        return pdfs;
    }
}
