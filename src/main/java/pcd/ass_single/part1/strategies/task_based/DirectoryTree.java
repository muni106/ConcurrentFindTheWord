package pcd.ass_single.part1.strategies.task_based;


import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class DirectoryTree {
    private final List<DirectoryTree> subDirectories;
    private final List<File> pdfs;

    public DirectoryTree(List<DirectoryTree> subDirectories, List<File> pdfs) {
        this.subDirectories = subDirectories;
        this.pdfs = pdfs;
    }

    public List<DirectoryTree> getSubDirectories() {
        return this.subDirectories;
    }

    public List<File> getPdfs() {
        return this.pdfs;
    }

    public static DirectoryTree fromDirectory(File dir) throws IOException {
        List<File> pdfs = new LinkedList<File>();
        List<DirectoryTree> subDirectories = new LinkedList<DirectoryTree>();
        for (File entry : dir.listFiles()) {
            if (entry.isDirectory()) {
                subDirectories.add(DirectoryTree.fromDirectory(entry));
            } else if (entry.getName().toLowerCase().endsWith("pdf")){
                pdfs.add(entry);
            }
        }
        return new DirectoryTree(subDirectories, pdfs);
    }
}
