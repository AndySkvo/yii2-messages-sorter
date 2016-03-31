package ru.andyskvo.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Skvorcov on 30.03.2016.
 */
public class Main {
    public static void main(String[] args) {
        String fileName = args[0].trim();

        Path path = Paths.get(fileName);
        Charset charset = Charset.forName("UTF-8");

        try(BufferedReader bufferedReader = Files.newBufferedReader(path, charset)) {
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

            String[]allPairs = dict.split(",");

            TreeMap<String, String> map = new TreeMap<>();

            for (String pair : allPairs) {
                pair = pair.replaceAll("'", "");
                String[] arr = pair.split("=>");
                String key = arr[0].trim();
                String val = arr[1].trim();
                map.put(key, val);
            }

            try(PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(path, charset))) {
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
}
