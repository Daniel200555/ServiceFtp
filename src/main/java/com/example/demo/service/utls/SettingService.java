package com.example.demo.service.utls;

import com.example.demo.dto.FileInfoDTO;

import java.util.List;

public sealed interface SettingService permits UtilServiceImpl {

    List<FileInfoDTO> sortByName(List<FileInfoDTO> list);

    List<FileInfoDTO> sortByData(List<FileInfoDTO> list);

}
