package com.example.demo.controllers;

import com.example.demo.dto.FileInfoDTO;
import com.example.demo.service.ftpservice.FtpServiceImpl;
import com.example.demo.service.stream.StreamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@RestController
public class FtpController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpController.class);

    @Autowired
    private FtpServiceImpl ftpService;

    @Autowired
    private StreamService streamService;

    @Autowired
    private Environment environment;

    private String host;
    private String port;
    private String user;
    private String password;
    private String pathStandard;

    public FtpController(StreamService streamService, FtpServiceImpl ftpService, Environment environment) {
        this.streamService = streamService;
        this.ftpService = ftpService;
        this.environment = environment;
        this.host = environment.getProperty("ftp.host");
        this.port = environment.getProperty("ftp.port");
        this.user = environment.getProperty("ftp.user");
        this.password = environment.getProperty("ftp.password");
        this.pathStandard = environment.getProperty("ftp.pathStandard");
    }

    @GetMapping(value = "/stream", produces = "video/mp4")
    public Mono<Resource> getStream(@RequestParam String title) {
        return streamService.getVideo(title);
    }

    @GetMapping("/create/directory")
    public Boolean createDirectory(@RequestParam("path")String path) {
        try {
            ftpService.createDirectory(path, ftpService.loginFTP(getHost(), getPort(), getUser(), getPassword()));
            return true;
        } catch (IOException e) {
            LOGGER.info("ERROR: cannot create directory in ftp server!!!");
        }
        return false;
    }

    @GetMapping("/delete/directory")
    public Boolean deleteDirectory(@RequestParam("path")String path) {
        try {
            ftpService.deleteDirectory(path, ftpService.loginFTP(getHost(), getPort(), getUser(), getPassword()));
            return true;
        } catch (IOException e) {
            LOGGER.info("ERROR: cannot delete directory in ftp server!!!");
        }
        return false;
    }

    @GetMapping("/list")
    public List<FileInfoDTO> list(@RequestParam String path) throws IOException {
        LOGGER.info(getPathStandard() + path);
        List<FileInfoDTO> infoDTOList = ftpService.frontInfo(getPathStandard() + path, ftpService.loginFTP(getHost(), getPort(), getUser(), getPassword()));
        return infoDTOList;
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
