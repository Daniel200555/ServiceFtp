package com.example.demo.parsing;

import java.util.ArrayList;
import java.util.List;

public class FileLink {

    public static List<String> parseLink(String dir) {
        List<String> ids = new ArrayList<>();
        StringBuilder temp = new StringBuilder();
        for (int i = 1; i < dir.length(); i++) {
            if (dir.charAt(i) == '/') {
                ids.add(temp.toString());
                temp.setLength(0); // Clear the StringBuilder for the next ID
            } else {
                temp.append(dir.charAt(i));
            }
        }
        return ids;
    }

}
