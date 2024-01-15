package com.example.demo.service.utls;

import com.example.demo.dto.FileInfoDTO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public non-sealed class UtilServiceImpl implements SettingService {
    @Override
    public List<FileInfoDTO> sortByName(List<FileInfoDTO> list) {
        Collections.sort(list, new Comparator<FileInfoDTO>() {
            @Override
            public int compare(FileInfoDTO o1, FileInfoDTO o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return list;
    }

    @Override
    public List<FileInfoDTO> sortByData(List<FileInfoDTO> list) {
        return null;
    }
}
