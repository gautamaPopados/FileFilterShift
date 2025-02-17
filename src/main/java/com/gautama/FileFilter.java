package com.gautama;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileFilter {
    private static String resultDir = "";
    private static String prefix = "";
    private static boolean appendMode = false;
    private static boolean fullStats = false;


    public static void main(String[] args) {
        List<String> inputFiles = new ArrayList<>();
        parseArguments(args, inputFiles);
        processFiles(inputFiles);
    }

    private static void parseArguments(String[] args, List<String> inputFiles) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-o":
                    if (++i < args.length) resultDir = args[i];
                    break;
                case "-p":
                    if (++i + 1 < args.length) prefix = args[i];
                    break;
                case "-a":
                    appendMode = true;
                    break;
                case "-f":
                    fullStats = true;
                    break;
                case "-s":
                    fullStats = false;
                    break;
                default:
                    inputFiles.add(args[i]);
            }
        }
    }

    private static void processFiles(List<String> inputFiles) {
        Map<String, List<String>> categorizedData = new HashMap<>();
        categorizedData.put("integers", new ArrayList<>());
        categorizedData.put("floats", new ArrayList<>());
        categorizedData.put("strings", new ArrayList<>());

        for (String inputFile : inputFiles) {
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (isInteger(line)) {
                        categorizedData.get("integers").add(line);
                    } else if (isFloat(line)) {
                        categorizedData.get("floats").add(line);
                    } else {
                        categorizedData.get("strings").add(line);
                    }
                }
            } catch (IOException e) {
                System.err.println("Ошибка при чтении файла: " + inputFile + " - " + e.getMessage());
            }
        }

        writeDataToFile("integers", categorizedData.get("integers"));
        writeDataToFile("floats", categorizedData.get("floats"));
        writeDataToFile("strings", categorizedData.get("strings"));

    }

    private static void writeDataToFile(String type, List<String> data) {
        if (data.isEmpty()) return;

        Path filePath = Paths.get(resultDir, prefix + type + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), appendMode))) {
            for (String entry : data) {
                writer.write(entry);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл " + filePath + " - " + e.getMessage());
        }
    }


    private static boolean isInteger(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isFloat(String str) {
        try {
            Double.parseDouble(str);
            return str.contains(".");
        } catch (NumberFormatException e) {
            return false;
        }
    }
}