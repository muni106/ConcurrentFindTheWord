package pcd.ass_single.part1;


import pcd.ass_single.part1.events.ExtractionEvent;
import pcd.ass_single.part1.events.ExtractionEventType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SearchView extends JFrame implements ActionListener, ModelObserver {
    private JTextField directoryPathField;
    private JTextField searchWordField;
    private JTextField countFiles;
    private JTextField countPdfFiles;
    private JTextField countPdfFilesWithWord;
    private final SearchController controller;

    public SearchView(SearchController controller) {
        this.controller = controller;

        setSize(1000, 300);
        setResizable(false);
        setTitle("PCD assignment 1");

        // Input fields for directory and word
        directoryPathField = new JTextField(20);
        directoryPathField.setToolTipText("Enter directory path");
        searchWordField = new JTextField(15);
        searchWordField.setToolTipText("Enter word to search");

        // Start button
        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> handleExtraction(ExtractionEventType.START));

        JButton stopButton = new JButton("Stop");
        startButton.addActionListener(e -> handleExtraction(ExtractionEventType.STOP));

        JButton resumeButton = new JButton("Resume");
        startButton.addActionListener(e -> handleExtraction(ExtractionEventType.RESUME));


        JButton suspendButton = new JButton("Suspend");
        startButton.addActionListener(e -> handleExtraction(ExtractionEventType.SUSPEND));

        countFiles = new JTextField(20);
        countFiles.setEditable(false);
        countPdfFiles = new JTextField(20);
        countPdfFiles.setEditable(false);
        countPdfFilesWithWord = new JTextField(10);
        countPdfFilesWithWord.setEditable(false);

        JPanel inputPanel = new JPanel();
        inputPanel.add(directoryPathField);
        inputPanel.add(searchWordField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(resumeButton);
        buttonPanel.add(suspendButton);


        JPanel resultPanel = new JPanel();
        resultPanel.add(countFiles);
        resultPanel.add(countPdfFiles);
        resultPanel.add(countPdfFilesWithWord);

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(resultPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(-1);
            }
        });
    }

    private void handleExtraction(ExtractionEventType action) {
        String directoryPath = directoryPathField.getText().trim();
        String searchWord = searchWordField.getText().trim();
        if (directoryPath.isEmpty() || searchWord.isEmpty()) {
            log("Input Error", "please enter valid inputs", JOptionPane.WARNING_MESSAGE);
        } else {
            controller.processEvent(new ExtractionEvent(action, directoryPath, searchWord));
        }
    }

    @Override
    public void modelUpdated(SearchModel model) {
        SwingUtilities.invokeLater(() -> {
            countFiles.setText("num files: " + model.getCountFiles());
            countPdfFiles.setText("num pdf files: " + model.getCountPdfFiles());
            countPdfFilesWithWord.setText("word in: " + model.getCountPdfFilesWithWord());
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        log("ok", "this is the action performed: " + e.toString(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void log(String title, String msg, int type) {
        JOptionPane.showMessageDialog(this, msg, title, type);
    }
}
