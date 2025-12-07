package com.lhr.filetransfer.dto;

/**
 * @author lhr
 * @additional_information
 */
public class FileInfo {
    private String fileName;
    private String fileSize;

    public FileInfo() {}

    public FileInfo(String fileName, String fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    // Getters and Setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
}