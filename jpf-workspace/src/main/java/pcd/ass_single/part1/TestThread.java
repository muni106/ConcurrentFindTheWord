package pcd.ass_single.part1;

public class TestThread {

    public static void main(String[] args) {

        SearchModel model = new SearchModel();
        SearchController controller = new SearchController(model);

        model.startFromScratch("test_10_000__50_000", "goku");
        model.startFromScratch("test_recursive_1000_10000", "goku");
    }
}
