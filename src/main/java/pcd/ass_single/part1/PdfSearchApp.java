package pcd.ass_single.part1;

import pcd.ass_single.part1.strategies.thread.ThreadPoolSearch;

public class PdfSearchApp {

    public static void main(String[] args) {
        // here you can change strategy, changing the SeachModel constructor's argument
        SearchModel model = new SearchModel(new ThreadPoolSearch());
        SearchController controller = new SearchController(model);
        SearchView view = new SearchView(controller);

        model.addObserver(view);
        view.setVisible(true);
    }
}
