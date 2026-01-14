package pcd.ass_single.part1.benchmarks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BenchmarkCommon {

    public static List<File> collectPdfFiles(String directoryPath) {
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


    public static void log(String msg) {
        System.out.println(msg);
    }
}
