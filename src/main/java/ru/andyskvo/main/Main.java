package ru.andyskvo.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
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
    public static void main(String[] args) throws IOException {
        String fileName = args[0].trim();

        Path path = Paths.get(fileName);
        Charset charset = Charset.forName("UTF-8");
        //BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        BufferedReader bufferedReader = Files.newBufferedReader(path, charset);
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

        String dict = allStrings.substring(openTagIndex, closeTagIndex);

        String[]allPairs = dict.split(",");
        LinkedList<String> list = new LinkedList<>();
        for (String pair : allPairs) {
            list.add(pair);
        }

        TreeMap<String, String> map = new TreeMap<>();

        for (String pair : list) {
            String[] arr = pair.split("=>");
            String key = arr[0].replace("'", "").trim();
            String val = arr[1].replaceAll("'", "").trim();
            map.put(key, val);
        }

        //PrintWriter printWriter = new PrintWriter(new FileWriter(fileName));
        PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(path));
        printWriter.println(openTag);
        for (Map.Entry<String, String> p : map.entrySet()) {
            printWriter.println("    '" + p.getKey() + "' => '" + p.getValue() + "',");
        }
        printWriter.println(closeTag);

        printWriter.close();
    }
}
