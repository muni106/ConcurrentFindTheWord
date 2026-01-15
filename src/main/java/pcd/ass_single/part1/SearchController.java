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
                try {
                    log("[Controller] processing the event: " + event);
                    Thread.sleep(1000);
                    switch (event.eventType()) {
                        case START -> {
                            model.stop();
                            // TODO try to remove these and see if it works
                            new Thread(() -> {
                                model.startFromScratch((event.directoryPath()), event.searchWord());
                            }).start();
                        }
                        case STOP -> {
                            model.stop();
                        }
                        case SUSPEND -> {
                            model.suspend();
                        }
                        case RESUME -> {
                            model.resume();
                        }
                    }
                    log("[Controller] event processing done");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void log(String msg) {
        System.out.println(msg);
    }
}
