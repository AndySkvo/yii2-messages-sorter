package ru.andyskvo.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Skvorcov on 30.03.2016.
 */
public class Main {
    private static LinkedList<Path> fileList = new LinkedList<>();

    public static void main(String[] args) {
        try {
            final String URI = args[0].trim();
            if (validDirectory(URI)) {
                Path path = Paths.get(URI);
                findFiles(path);
                for (Path p : fileList) {
                    System.out.print(p);
                    sortMessages(p);
                    System.out.println(" SUCCESS!");
                }
            } else {
                System.out.println(URI + "is not directory");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Empty argument");
        }
    }

    private static void findFiles(Path path) {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (Path file : directoryStream) {
                if (Files.isDirectory(file)) {
                    //is directory
                    findFiles(file);
                } else {
                    //is file
                    fileList.add(file);
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private static void sortMessages(Path path) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            String lineContents;
            String allStrings = "";
            while ((lineContents = bufferedReader.readLine()) != null) {
                allStrings = allStrings + lineContents;
            }

            int openTagIndex = allStrings.indexOf('[') + 1;
            String openTag = allStrings.substring(0, openTagIndex);

            if (openTag.contains("phpreturn")) {
                openTag = openTag.replace("php", "php\n");
            }

            int closeTagIndex = allStrings.indexOf(']');
            String closeTag = allStrings.substring(closeTagIndex);

            String dict = allStrings.substring(openTagIndex, closeTagIndex).trim();

            String[] allPairs = dict.split(",");

            TreeMap<String, String> map = new TreeMap<>();

            for (String pair : allPairs) {
                pair = pair.replaceAll("'", "");
                String[] arr = pair.split("=>");
                String key = arr[0].trim();
                String val = arr[1].trim();
                map.put(key, val);
            }

            try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(path))) {
                printWriter.println(openTag);
                for (Map.Entry<String, String> p : map.entrySet()) {
                    printWriter.println("    '" + p.getKey() + "' => '" + p.getValue() + "',");
                }
                printWriter.println(closeTag);

            } catch (SecurityException e) {
                System.out.println("Write access error");
            }

        } catch (IOException e) {
            System.out.println("File " + path + " not found");
        }
    }

    private static boolean validDirectory(String uri) {
        String msg = "messages";
        int msgIndex = uri.indexOf(msg);
        if (msgIndex > -1) {
            return true;
        }
        return false;
    }
}
