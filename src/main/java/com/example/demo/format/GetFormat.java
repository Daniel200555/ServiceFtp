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
            if (name.charAt(i) == from) {
                temp = i;
            }
        }
        for (int i = temp + 1; i < num; i++)
            result += name.charAt(i);
        return result;
    }

    public String getPath(String path) {
        String tempStr = "";
        String result = "";
        int num = path.length();
        int temp = 0;
        for (int i = 0; i < num; i++) {
            if (tempStr.equals("files")) {
                temp = i;
                break;
            }
            if (path.charAt(i) == '/')
                tempStr = "";
            else
                tempStr += path.charAt(i);
        }
        for (int i = temp; i < num; i++)
            result += path.charAt(i);
        return result;
    }

    public String getWatch(String path) {
        int num = path.length();
        int temp = 0;
        String result = "";
        for (int i = 0; i < num; i++) {
            if (path.charAt(i) == '/') {
                temp = i;
                break;
            }
        }
        for (int i = temp + 1; i < num; i++)
            result += path.charAt(i);
        return result;
    }

    public String getJName(String name) {
        int num = name.length();
        int temp = 0;
        String result = "";
        for (int i = 0; i < num; i++) {
            if (name.charAt(i) == '.')
                temp = i;
        }
        for (int i = 0; i < temp; i++)
            result += name.charAt(i);
        return result;
    }

}
