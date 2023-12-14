package com.example.demo.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoDTO {

    private String path;

    private String name;

    private String type;

    private String format;

    private Boolean isFile;

    private Boolean isDirectory;

}
