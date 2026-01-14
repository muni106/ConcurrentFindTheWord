package pcd.ass_single.part1.strategies.reactive_prog;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import pcd.ass_single.part1.strategies.PdfWordSearcher;

import java.io.File;
import java.io.IOException;
import java.util.List;
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.flowables.ConnectableFlowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pcd.ass_single.part1.SearchModel;

public class RxJavaSearcher implements PdfWordSearcher {
    private ConnectableFlowable<Integer> getHotPdfStream(List<File> pdfs, String word, SearchModel model) throws IOException {
        Flowable<Integer> source = Flowable.create(emitter -> {
            try {
                for  (int i = 0; i < pdfs.size(); i++) {
                    // log("ciao " + i + " " + pdfs.get(i));
                    int searchResult = containsWord(pdfs.get(i), word);

                    if (searchResult == 1) {
                        model.incCountPdfFilesWithWord();
                    }
                    emitter.onNext(searchResult);
                }
                emitter.onComplete();
            } catch (Exception ex) {
                emitter.onError(ex);
                ex.printStackTrace();
                log("exit");
            }
        }, BackpressureStrategy.BUFFER);

        ConnectableFlowable<Integer> hotObservable = source.publish();
        hotObservable.connect();
        return hotObservable;
    }

    @Override
    public void extractText(List<File> pdfs, String word, SearchModel model) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            ConnectableFlowable<Integer> source = getHotPdfStream(pdfs, word, model);
            source
                    .onBackpressureBuffer(5_000, () -> log("Buffer is too large"))
                    .reduce(0, Integer::sum)
                    .observeOn(Schedulers.computation())
                    .blockingSubscribe(res -> log("total items: " + res), err -> log("error"));
        } finally {
//            long time = System.currentTimeMillis() - startTime;
//            log("computation time: " + time);
//            Schedulers.shutdown();
        }
    }

    private Integer containsWord(File pdf, String word) throws IOException {
        PDDocument document = PDDocument.load(pdf);

        AccessPermission ap = document.getCurrentAccessPermission();
        if (!ap.canExtractContent()) {
            throw new IOException("You do not have permission to extract text");
        }
        PDFTextStripper stripper = new PDFTextStripper();

        String text = stripper.getText(document);

        if (text.contains(word)) {
            document.close();
            return 1;
        }

        document.close();
        return 0;
    }

    static private void log(String msg) {
        System.out.println("[" + Thread.currentThread().getName() + " ] " + msg);
    }

}
