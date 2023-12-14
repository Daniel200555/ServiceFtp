package com.example.demo.service.ftpservice;

import com.example.demo.dto.FileInfoDTO;
import com.example.demo.format.GetFormat;
import com.example.demo.message.LogMessage;
import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public non-sealed class FtpServiceImpl implements FtpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpServiceImpl.class);

    public FTPClient loginFTP(String hostname, int port, String username, String password) throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.addProtocolCommandListener(new ProtocolCommandListener() {
            @Override
            public void protocolCommandSent(ProtocolCommandEvent protocolCommandEvent) {
                System.out.printf("[%s][%d] Command sent: [%s]-%s", Thread.currentThread().getName(),
                        System.currentTimeMillis(),
                        protocolCommandEvent.getCommand(),
                        protocolCommandEvent.getMessage());
            }

            @Override
            public void protocolReplyReceived(ProtocolCommandEvent protocolCommandEvent) {
                System.out.printf("[%s][%d] Reply received : %s", Thread.currentThread().getName(),
                        System.currentTimeMillis(),
                        protocolCommandEvent.getMessage());
            }
        });
        ftpClient.connect(hostname, port);
        System.out.printf("connect");
        ftpClient.login(username, password);
        System.out.printf("login");
        return ftpClient;
    }

    public void createDirectory(String path, FTPClient ftpClient) throws IOException {
        System.out.println();
        System.out.printf("[createDirectory][%d] Is success to create directory : %s -> %b",
                System.currentTimeMillis(),
                path,
                ftpClient.makeDirectory(path));
        System.out.println();
    }

    public void deleteDirectory(String path, FTPClient ftpClient) throws IOException {
        System.out.println();
        System.out.printf("[deleteDirectory][%d] Is success to delete directory : %s -> %b",
                System.currentTimeMillis(),
                path,
                ftpClient.removeDirectory(path));
        System.out.println();
    }

    public void deleteFile(String path, FTPClient ftpClient) throws IOException {
        System.out.println();
        System.out.printf("[deleteDirectory][%d] Is success to delete directory : %s -> %b",
                System.currentTimeMillis(),
                path,
                ftpClient.deleteFile(path));
    }

    @Override
    public FileInfoDTO getInfo(String path, FTPFile ftpFile) throws IOException {
//        return new FileInfoDTO(path + ftpFile.getName(), ftpFile.getName(), String.valueOf(ftpFile.getType()), ftpFile.isFile(), ftpFile.isDirectory());
        var message = new LogMessage("OK");
        FileInfoDTO infoDTO = new FileInfoDTO();
        infoDTO.setName(ftpFile.getName());
        if (ftpFile.isFile())
            infoDTO.setType(new GetFormat().getType(ftpFile.getName(), '.'));
        else
            infoDTO.setType(null);
        infoDTO.setPath(path + ftpFile.getName());
        infoDTO.setIsFile(ftpFile.isFile());
        infoDTO.setIsDirectory(ftpFile.isDirectory());
        System.out.println(message.message());
        return infoDTO;
    }

    @Override
    public List<FileInfoDTO> frontInfo(String path, FTPClient ftpClient) throws IOException {
        List<FileInfoDTO> list = new ArrayList<>();
        FileInfoDTO temp;
        for (FTPFile ftpFile : ftpClient.listFiles(path)) {
            temp = getInfo(path, ftpFile);
            list.add(temp);
            System.out.println(new LogMessage("ADD"));
        }
        return list;
    }
}
