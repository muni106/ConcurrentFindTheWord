package pcd.ass_single.part1.strategies.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import pcd.ass_single.part1.SearchModel;
import pcd.ass_single.part1.strategies.PdfWordSearcher;

import java.io.File;
import java.util.List;


public class ActorBasedSearcher implements PdfWordSearcher {

    static class RequesterActor extends  AbstractActor {
        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(Integer.class, count -> {
                        log("Number of pdfs with that word: " + count );
                        getContext().getSystem().terminate();
                    })
                    .build();
        }
    }

    // TODO fix logic
    @Override
    public void extractText(List<File> pdfs, String word, SearchModel model) throws Exception {
        ActorSystem actorSystem = ActorSystem.create("PdfCounter");
        ActorRef counter = actorSystem.actorOf(Props.create(PdfAnalyzerActor.class));

        for (File pdf : pdfs) {
            counter.tell(new PdfAnalyzerActor.PdfWordMessage(model, pdf, word), ActorRef.noSender());
        }

        ActorRef requester = actorSystem.actorOf(Props.create(RequesterActor.class));
        counter.tell(new PdfAnalyzerActor.GetCount(), requester);
    }


    static private void log(String msg) {
        System.out.println("[" + Thread.currentThread().getName() + " ] " + msg);
    }
}
