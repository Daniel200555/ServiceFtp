package com.example.demo.controllers;

import com.example.demo.entry.FileData;
import com.example.demo.dto.FileInfoDTO;
import com.example.demo.entry.User;
import com.example.demo.exception.DeleteDirException;
import com.example.demo.format.GetFormat;
import com.example.demo.parsing.FileLink;
import com.example.demo.repository.MongoRep;
import com.example.demo.service.ftpservice.FtpServiceImpl;
import com.example.demo.service.mongo.SequenceGeneratorService;
import com.example.demo.service.mongo.files.FileService;
import com.example.demo.service.stream.StreamService;
import com.example.demo.service.utls.SettingService;
import com.example.demo.service.zip.ZipService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class FtpController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpController.class);

    @Autowired
    private FtpServiceImpl ftpService;

    @Autowired
    private StreamService streamService;

    @Autowired
    private Environment environment;

    @Autowired
    private SettingService settingService;

    @Autowired
    private ZipService zipService;

    @Autowired
    private MongoRep mongoRep;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    private String host;
    private String port;
    private String user;
    private String password;
    private String pathStandard;

    public FtpController(StreamService streamService, FtpServiceImpl ftpService, Environment environment, SettingService settingService) {
        this.settingService = settingService;
        this.streamService = streamService;
        this.ftpService = ftpService;
        this.environment = environment;
        this.host = environment.getProperty("ftp.host");
        this.port = environment.getProperty("ftp.port");
        this.user = environment.getProperty("ftp.user");
        this.password = environment.getProperty("ftp.password");
        this.pathStandard = environment.getProperty("ftp.pathStandard");
    }

    @PostMapping(value = "/test/add/user")
    public User addUser(@RequestParam String nickname) {
        List<FileData> files = new ArrayList<>();
        List<FileData> shared = new ArrayList<>();

        User user = new User();
        user.setFiles(files);
        user.setSharedFiles(shared);
        user.setNickname(nickname);
        user.setId(sequenceGeneratorService.getNextSequence("database_sequence"));
        return mongoRep.save(user);
    }

    @PostMapping("/test/add/file")
    public User updateUser(@RequestParam("nickname") String nickname, @RequestParam("dir") String dir, @RequestParam MultipartFile[] file) {
        User usertemp = mongoRep.findByNickname(nickname);
        List<FileData> files = usertemp.getFiles();
        for (MultipartFile f: file)
            files = FileService.action(usertemp.getFiles(), dir).add(f, dir, nickname);
        usertemp.setFiles(files);
        return mongoRep.save(usertemp);
    }

    @PostMapping("/test/add/folder")
    public User addFolder(@RequestParam("nickname") String nickname, @RequestParam("dir") String dir, @RequestParam("name") String name) {
        User usertemp = mongoRep.findByNickname(nickname);
        List<FileData> files = FileService.action(usertemp.getFiles(), dir).add(name, dir, nickname);
        usertemp.setFiles(files);
        return mongoRep.save(usertemp);
    }

    @PostMapping("/test/delete")
    public User deleteFile(@RequestParam("nickname") String nickname, @RequestParam("dir") String dir) {
        User user = mongoRep.findByNickname(nickname);
        List<FileData> files = FileService.action(user.getFiles(), dir).remove();
        user.setFiles(files);
        return mongoRep.save(user);
    }

    @PostMapping("/test/rename")
    public User renameFile(@RequestParam("nickname") String nickname, @RequestParam("dir") String dir, @RequestParam("name") String name) {
        User user = mongoRep.findByNickname(nickname);
        List<FileData> files = FileService.action(user.getFiles(), dir).rename(name);
        user.setFiles(files);
        return mongoRep.save(user);
    }

    @GetMapping("/test/show")
    public List<FileData> showFiles(@RequestParam("nickname") String nickname, @RequestParam("dir") String dir) {
        User user = mongoRep.findByNickname(nickname);
        return FileService.action(user.getFiles(), dir).showFile();
    }

    @GetMapping("/test/find")
    public List<FileData> findFiles(@RequestParam("nickname") String nickname, @RequestParam("name") String name, @RequestParam("dir") String dir) {
        User user = mongoRep.findByNickname(nickname);
        System.out.println("In method");
        return FileService.action().findFile(user.getFiles(), name);
    }

    @GetMapping(value = "/stream/video", produces = "video/mp4")
    public Mono<Resource> getStream(@RequestParam String title) {
        return streamService.getVideo(title);
    }

    @GetMapping(value = "/stream/picture", produces = "picture/png")
    public Mono<Resource> getStreamPicture(@RequestParam String title) { return streamService.getPicture(title); }

    @GetMapping(value = "/download/zip/{title}", produces = "application/zip")
    public Mono<Resource> getStreamZip(@RequestParam("user") String user, @PathVariable("title") String title) throws IOException {
//        String result = new String(path.getBytes("UTF-8"), "ISO-8859-1");
        return streamService.getZip("C:\\Users\\serda\\temp\\" + user + "\\" + title);
    }
////        String result = file + ".zip";
//        HttpHeaders header = new HttpHeaders();
//        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "Евангилион.zip");
//        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
//        header.add("Pragma", "no-cache");
//        header.add("Expires", "0");
//        return ResponseEntity.ok().headers(header).contentType(MediaType.parseMediaType("application/octet-stream")).body(streamService.getZip("C:\\Users\\serda\\temp\\" + user + "\\" + file));    }

    @GetMapping("/compress")
    public String compress(@RequestParam("path") String path, @RequestParam("user") String user, @RequestParam("name") String name) throws IOException {
        String temp = "/home/danya/ftp/files/" + path;
        String result = new String(temp.getBytes("UTF-8"), "ISO-8859-1");
        String p = zipService.zipFolder(temp, user, name, result, ftpService.loginFTP(getHost(), getPort(), getUser(), getPassword()));
        if (p == null)
            return null;
        else
            return p;
    }

    @GetMapping("/create/directory")
    public Boolean createDirectory(@RequestParam("path")String path) {
        try {
            String result = new String(path.getBytes("UTF-8"), "ISO-8859-1");
            ftpService.createDirectory(result, ftpService.loginFTP(getHost(), getPort(), getUser(), getPassword()));
            return true;
        } catch (IOException e) {
            LOGGER.info("ERROR: cannot create directory in ftp server!!!");
        }
        return false;
    }

    @GetMapping("/delete/directory")
    public boolean deleteDirectory(@RequestParam("path")String path) throws DeleteDirException, UnsupportedEncodingException {
        String temp = getPathStandard() + path;
        String result = new String(temp.getBytes("UTF-8"), "ISO-8859-1");
        try {
            ftpService.deleteDirectory(result, ftpService.loginFTP(getHost(), getPort(), getUser(), getPassword()));
            return true;
        } catch (IOException e) {
            LOGGER.info("ERROR: cannot delete directory in ftp server!!!");
            throw new DeleteDirException(path);
        }
    }

    @GetMapping("/list")
    public List<FileInfoDTO> list(@RequestParam("path") String path) throws IOException {
        LOGGER.info(getPathStandard() + path);
        String temp = getPathStandard() + path;
        String result = new String(temp.getBytes("UTF-8"), "ISO-8859-1");
        List<FileInfoDTO> infoDTOList = ftpService.frontInfo(result, path, ftpService.loginFTP(getHost(), getPort(), getUser(), getPassword()));
        return infoDTOList;
    }

    @PostMapping("/upload")
    public ResponseEntity uploadFile(@RequestParam("file")MultipartFile uploadFile) throws IOException {
        InputStream inputStream = new BufferedInputStream(uploadFile.getInputStream());
        ftpService.uploadFile(inputStream, "", ftpService.loginFTP(getHost(), getPort(), getUser(), getPassword()));
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/uploadFolder")
    public Map<String, Boolean> uploadFolder(@RequestParam("folder") MultipartFile[] folder) {
        Map<String, Boolean> data = new HashMap<>();
        for (int i = 0; i < folder.length; i++) {
            data.put(folder[i].getOriginalFilename(), true);
        }
        return data;
    }

    @GetMapping("/delete/compress")
    public boolean delete(@RequestParam("user") String user) throws DeleteDirException {
        File file = new File("C:\\Users\\serda\\temp\\" + user);
        try {
            FileUtils.deleteDirectory(file);
            return true;
        } catch (IOException e) {
            throw new DeleteDirException(file.getPath());
        }
    }


//    @GetMapping("/download")
//    public ResponseEntity<byte[]> download(@RequestParam String path) throws Exception {
//        String temp = getPathStandard() + path;
//        String result = new String(temp.getBytes("UTF-8"), "ISO-8859-1");
////        byte[] array = ftpService.downloadFile(result, ftpService.loginFTP(getHost(), getPort(), getUser(), getPassword()));
////        File file = new File(array);
//        return new ResponseEntity<>(ftpService.downloadFile(result, ftpService.loginFTP(getHost(), getPort(), getUser(), getPassword())), HttpStatus.OK);
//    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> download(@RequestParam("path") String path, @RequestParam("name") String name) throws Exception {
        String temp = getPathStandard() + path;
        String result = new String(temp.getBytes("UTF-8"), "ISO-8859-1");
        byte[] array = ftpService.downloadFile(result, ftpService.loginFTP(getHost(), getPort(), getUser(), getPassword()));
        InputStream i = new ByteArrayInputStream(array);
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return ResponseEntity.ok().headers(header).contentLength(i.available()).contentType(MediaType.parseMediaType("application/octet-stream")).body(new InputStreamResource(i));
    }

    public String getPathStandard() {
        return this.pathStandard;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return Integer.parseInt(this.port);
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }

}
