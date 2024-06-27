package com.example.demo.service.mongo.files;

import com.example.demo.entry.FileData;
import com.example.demo.format.GetFormat;
import com.example.demo.parsing.FileLink;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileService {

    public static Actions action(List<FileData> files, String dir) {
        return new Actions(files, findFileData(files, FileLink.parseLink(dir)), dir);
    }

    public  static Actions action() {
        System.out.println("In action");
        return new Actions();
    }

    public static FileData findFileData(List<FileData> fileDataList, List<String> ids) {
        if (ids.isEmpty())
            return null;
        String idToFind = ids.get(0);
        for (FileData fileData : fileDataList) {
            if (fileData.getFile_id() == Long.parseLong(idToFind)) {
                if (ids.size() == 1)
                    return fileData;
                else
                    return findFileData(fileData.getFiles(), ids.subList(1, ids.size()));
            }
        }
        return null;
    }

}
