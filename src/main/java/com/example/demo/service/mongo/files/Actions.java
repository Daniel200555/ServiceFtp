package com.example.demo.service.mongo.files;

import com.example.demo.entry.FileData;
import com.example.demo.format.GetFormat;
import com.example.demo.parsing.FileLink;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;

public class Actions {

    private List<FileData> files;
    private String dir;
    private FileData file;

    public Actions(List<FileData> files, FileData file, String dir) {
        this.files = files;
        this.dir = dir;
        this.file = file;
    }

    public Actions() {

    }

    public List<FileData> add(MultipartFile f, String dir, String nickname) {
        if (FileLink.parseLink(dir).isEmpty()) {
            this.files.add(addFile(f, dir, nickname, this.files));
            return this.files;
        } else {
            FileData targetFileData = file;
            if (targetFileData == null)
                return null;
            System.out.println(targetFileData.getFilename());
            if (targetFileData.getFiles() == null)
                targetFileData.setFiles(new ArrayList<>());
            FileData uploadedFile = addFile(f, dir, nickname, targetFileData.getFiles());
            targetFileData.getFiles().add(uploadedFile);

            return this.files;
        }
    }

    public List<FileData> add(String name, String dir, String nickname) {
        if (FileLink.parseLink(dir).isEmpty()) {
            this.files.add(addDirectory(name, dir, nickname, this.files));
            return this.files;
        } else {
            FileData targetFileData = file;
            if (targetFileData == null)
                return null;
            System.out.println(targetFileData.getFilename());
            if (targetFileData.getFiles() == null)
                targetFileData.setFiles(new ArrayList<>());
            FileData uploadedFile = addDirectory(name, dir, nickname, targetFileData.getFiles());
            targetFileData.getFiles().add(uploadedFile);
            return this.files;
        }
    }

    private FileData addFile(MultipartFile f, String dir, String nickname, List<FileData> fs) {
        long id = idForFile(fs);
        return new FileData.Builder()
                .setFiles(new ArrayList<>())
                .setFileName(GetFormat.getOnlyName(f.getOriginalFilename()))
                .setId(id)
                .setDir(nickname + dir + (id) + "." + GetFormat.getType(f.getOriginalFilename(), '.'))
                .isFile(GetFormat.checkIsFile(f.getOriginalFilename()))
                .setFormat(GetFormat.getType(f.getOriginalFilename(), '.'))
                .setType(GetFormat.formatFile(GetFormat.getType(f.getOriginalFilename(), '.')))
                .build();
    }

    private FileData addDirectory(String name, String dir, String nickname, List<FileData> fs) {
        long id = idForFile(fs);
        return new FileData.Builder()
                .setFiles(new ArrayList<>())
                .setFileName(GetFormat.getOnlyName(name))
                .setId(id)
                .setDir(nickname + dir + (id))
                .isFile(false)
                .setFormat("NULL")
                .setType("NULL")
                .build();
    }

    public List<FileData> remove() {
        List<String> ids = FileLink.parseLink(dir);
        String id = ids.get(ids.size() - 1);
        ids.remove(ids.size() - 1);
        if (ids.isEmpty()) {
            List<String> i = new ArrayList<>();
            i.add(id);
            this.files.remove(FileService.findFileData(files, i));
            return this.files;
        } else {
            FileData targetFileData = FileService.findFileData(this.files, ids);
            if (targetFileData == null)
                return null;
            System.out.println(targetFileData.getFilename());
            if (targetFileData.getFiles() == null)
                targetFileData.setFiles(new ArrayList<>());
            targetFileData.getFiles().remove(file);
            return this.files;
        }
    }

    public List<FileData> showFile() {
        if (file == null)
            return files;
        else
            return file.getFiles();
    }

    public List<FileData> findFile(List<FileData> files, String name){
        List<FileData> result = new ArrayList<>();;
        for (FileData f : files) {
            if (f.getFilename().toLowerCase().contains(name.toLowerCase()))
                result.add(f);
            if (f.getFiles() != null && !f.getFiles().isEmpty())
                result.addAll(findFile(f.getFiles(), name));
        }
        return result;
    }

    public List<FileData> rename(String newName) {
        this.file.setFilename(newName);
        return this.files;
    }

    private boolean checkIfIdExist(long id, List<FileData> fs) {
        for (FileData f : fs) {
            if (f.getFile_id() == id)
                return true;
        }
        return false;
    }

    private long idForFile(List<FileData> fs) {
        if (fs == null) {
            return 1;
        }
        long id = fs.size();
        boolean b = true;
        if (fs.size() != 0) {
            while (b) {
                id = id + 1;
                b = checkIfIdExist(id, fs);
            }
        } else {
            return 1;
        }
        return id;
    }
}
