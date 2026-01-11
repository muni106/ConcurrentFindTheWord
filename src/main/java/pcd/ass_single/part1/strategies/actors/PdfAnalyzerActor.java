package pcd.ass_single.part1.strategies.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import pcd.ass_single.part1.SearchModel;

import java.io.File;
import java.io.IOException;

class PdfAnalyzerActor extends AbstractActor {
    static class GetCount {}

    static class PdfWordMessage {
        public final SearchModel model;
        public final File pdf;
        public final String word;

        public PdfWordMessage(SearchModel model, File pdf, String word) {
            this.model = model;
            this.pdf = pdf;
            this.word = word;
        }
    }

    private int count = 0;
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PdfWordMessage.class, pdfWordMessage -> {
                    this.count += containsWord(pdfWordMessage.pdf, pdfWordMessage.word, pdfWordMessage.model);
                })
                .match(GetCount.class, msg -> {
                    getSender().tell(count, ActorRef.noSender());
                })
                .build();
    }

    private Integer containsWord(File pdf, String word, SearchModel model) throws IOException {
        try (PDDocument document = PDDocument.load(pdf)) {
            AccessPermission ap = document.getCurrentAccessPermission();
            if (!ap.canExtractContent()) {
                throw new IOException("You do not have permission to extract text");
            }
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();
            if ( text.contains(word) ) {
                model.incCountPdfFilesWithWord();
                return 1;
            }
            return 0;
        }
    }

}
