package pcd.ass_single.part1.strategies.virtual_threads;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pcd.ass_single.part1.strategies.PdfWordSearcher;
import pcd.ass_single.part1.SearchModel;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;


public class VirtualThreadSearcher implements PdfWordSearcher {

    private static final Logger log = LoggerFactory.getLogger(VirtualThreadSearcher.class);

    @Override
    public void extractText(List<File> files, String word, SearchModel model) {

        Monitor m;

        if (files != null) {
            // i can decide the number of threads based on the number of files
            int numFiles = files.size();
            m = new Monitor(numFiles, model);

            Thread outputThread = Thread.ofVirtual().name("outputThread").unstarted(() -> {
                int value = m.get();
                System.out.println("final count: " + value);
            });



            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                IntStream.range(0, numFiles).forEach(i -> {
                    executor.submit(() -> {
                        Thread
                                .ofVirtual()
                                .name("workerThread[" + i + "]")
                                .start(() -> {
//                                System.out.println("Hello from " + Thread.currentThread());
                                    try {
                                        model.checkState();
                                        m.foundWord(containsWord(files.get(i), word));
                                    } catch (InterruptedException e) {
                                        executor.shutdownNow();
                                    }catch (IOException e) {
                                        m.foundWord(false);
                                    }
                                });
                    });
                });
            }

            outputThread.start();

            try {
                outputThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }else {
            System.err.println("No files found");
        }
    }

    private static boolean containsWord(File pdf, String word) throws IOException {
        PDDocument document = PDDocument.load(pdf);

        AccessPermission ap = document.getCurrentAccessPermission();
        if (!ap.canExtractContent()) {
            throw new IOException("You do not have permission to extract text");
        }
        PDFTextStripper stripper = new PDFTextStripper();

        String text = stripper.getText(document);

        if (text.contains(word)) {
            document.close();
            return true;
        }
        document.close();
        return false;
    }


}


