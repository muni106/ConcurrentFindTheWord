package pcd.ass_single.part1.strategies;

import pcd.ass_single.part1.SearchModel;

import java.io.File;
import java.util.List;

public interface PdfWordSearcher {
    void extractText(List<File> pdfs, String word, SearchModel model) throws Exception;
}
