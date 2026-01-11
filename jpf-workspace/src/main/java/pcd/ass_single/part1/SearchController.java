package pcd.ass_single.part1;


import pcd.ass_single.part1.events.ExtractionEvent;

public class SearchController {
    private SearchModel model;
    private boolean started;

    public SearchController(SearchModel model) {
        this.model = model;
        this.started = false;
    }

    public void processEvent(ExtractionEvent event) {
        try {
            new Thread(() -> {
                try {
                    log("[Controller] processing the event: " + event);
                    Thread.sleep(1000);
                    switch (event.eventType()) {
                        case START: model.startFromScratch(event.directoryPath(), event.searchWord());
                        case STOP: log("ciao");
                        case SUSPEND: log("ciao");
                        case RESUME: log("ciao");
                    }
                    log("[Controller] event processing done");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void log(String msg) {
        System.out.println(msg);
    }
}
