package com.example.demo.format;

public class GetFormat {

    public String formatFile(String name) {
        return switch (name) {
            case "jpg", "jpeg", "png" -> "PICTURE";
            case "mp4", "mov", "avi" -> "VIDEO";
            case default -> "NULL";
        };
    }

    public String getType(String name, char from) {
        int num = name.length();
        int temp = 0;
        String result = "";
        for (int i = 0; i < num; i++) {
            if (name.charAt(i) == from)
                temp = i;
        }
        for (int i = temp + 1; i < num; i++)
            result += name.charAt(i);
        return result;
    }

}
