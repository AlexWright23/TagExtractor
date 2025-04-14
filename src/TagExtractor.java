import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class TagExtractor extends JFrame {

    private JTextArea outputArea;
    private JLabel fileLabel;
    private Map<String, Integer> tagMap;
    private Set<String> stopWords;

    public TagExtractor() {
        super("Tag Extractor");
        tagMap = new TreeMap<>();
        stopWords = new TreeSet<>();
        setupGUI();
    }

    private void setupGUI() {
        JButton loadTextButton = new JButton("Load Text File");
        JButton loadStopWordsButton = new JButton("Load Stop Words File");
        JButton saveOutputButton = new JButton("Save Output");

        outputArea = new JTextArea(25, 60);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        fileLabel = new JLabel("No file selected");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loadTextButton);
        buttonPanel.add(loadStopWordsButton);
        buttonPanel.add(saveOutputButton);

        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(fileLabel, BorderLayout.SOUTH);

        loadTextButton.addActionListener(e -> loadTextFile());
        loadStopWordsButton.addActionListener(e -> loadStopWordsFile());
        saveOutputButton.addActionListener(e -> saveToFile());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadStopWordsFile() {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File stopWordsFile = chooser.getSelectedFile();
            stopWords.clear();
            try (Scanner scanner = new Scanner(stopWordsFile)) {
                while (scanner.hasNextLine()) {
                    String word = scanner.nextLine().trim().toLowerCase();
                    if (!word.isEmpty()) {
                        stopWords.add(word);
                    }
                }
                JOptionPane.showMessageDialog(this, "Stop words loaded: " + stopWords.size());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading stop words file, gg");
            }
        }
    }

    private void loadTextFile() {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File textFile = chooser.getSelectedFile();
            fileLabel.setText("File: " + textFile.getName());
            tagMap.clear();
            try (Scanner scanner = new Scanner(textFile)) {
                while (scanner.hasNext()) {
                    String word = scanner.next().toLowerCase().replaceAll("[^a-z]", "");
                    if (!word.isEmpty() && !stopWords.contains(word)) {
                        tagMap.put(word, tagMap.getOrDefault(word, 0) + 1);
                    }
                }
                displayTags();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading text file:(");
            }
        }
    }

    private void displayTags() {
        outputArea.setText("");
        for (Map.Entry<String, Integer> entry : tagMap.entrySet()) {
            outputArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
    }

    private void saveToFile() {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File outputFile = chooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(outputFile)) {
                for (Map.Entry<String, Integer> entry : tagMap.entrySet()) {
                    pw.println(entry.getKey() + ": " + entry.getValue());
                }
                JOptionPane.showMessageDialog(this, "File saved: " + outputFile.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file:(");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TagExtractor::new);
    }
}


