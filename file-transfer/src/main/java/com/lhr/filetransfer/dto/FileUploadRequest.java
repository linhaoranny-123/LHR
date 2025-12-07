package com.lhr.filetransfer.dto;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotNull;

/**
 * @author lhr
 * @additional_information
 */
public class FileUploadRequest {

    @NotNull(message = "文件不能为空")
    private MultipartFile file;

    @NotNull(message = "token不能为空")
    private String token;

    // getters and setters
    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}