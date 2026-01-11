package pcd.ass_single.part1.events;

public final class ExtractionEvent {
    private final ExtractionEventType eventType;
    private final String directoryPath;
    private final String searchWord;

    public ExtractionEvent(ExtractionEventType eventType, String directoryPath, String searchWord) {
        this.eventType = eventType;
        this.directoryPath = directoryPath;
        this.searchWord = searchWord;
    }

    public ExtractionEventType eventType() {
        return eventType;
    }

    public String directoryPath() {
        return directoryPath;
    }

    public String searchWord() {
        return searchWord;
    }
}
