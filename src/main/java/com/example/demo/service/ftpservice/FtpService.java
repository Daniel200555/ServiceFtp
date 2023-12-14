package com.example.demo.service.ftpservice;

import com.example.demo.dto.FileInfoDTO;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.util.List;

public sealed interface FtpService permits FtpServiceImpl {

    FTPClient loginFTP(String hostname, int port, String username, String password) throws IOException;

    void createDirectory(String path, FTPClient ftpClient) throws IOException;

    void deleteDirectory(String path, FTPClient ftpClient) throws IOException;

    void deleteFile(String path, FTPClient ftpClient) throws IOException;

    FileInfoDTO getInfo(String path, FTPFile ftpFile) throws IOException;

    List<FileInfoDTO> frontInfo(String path, FTPClient ftpClient) throws IOException;

}
