package com.lhr.filetransfer.service;

import com.lhr.filetransfer.dto.FileInfo;
import com.lhr.filetransfer.properties.FileUploadProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lhr
 * @additional_information
 * 获取文件列表
 */
@Service
public class FileListService {

    @Value("${app.file.uploadDir}")
    private String fileDirectory;

    /**
     * 获取文件列表
     */
    public List<FileInfo> getFileList() {
        List<FileInfo> fileList = new ArrayList<>();

        try {
            // 确保路径分隔符正确
            String normalizedPath = fileDirectory.replace("\\", File.separator);
            Path directoryPath = Paths.get(normalizedPath);
            File directory = directoryPath.toFile();

            // 检查目录是否存在
            if (!directory.exists() || !directory.isDirectory()) {
                throw new RuntimeException("目录不存在: " + directoryPath.toAbsolutePath());
            }

            // 获取目录下的所有文件
            File[] files = directory.listFiles(File::isFile);

            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    String fileSize = formatFileSize(file.length());

                    fileList.add(new FileInfo(fileName, fileSize));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("获取文件列表失败: " + e.getMessage(), e);
        }

        return fileList;
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size <= 0) return "0 B";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return String.format("%.2f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    /**
     * 检查目录是否存在，不存在则创建
     */
    public void ensureDirectoryExists() {
        try {
            String normalizedPath = fileDirectory.replace("\\", File.separator);
            Path directoryPath = Paths.get(normalizedPath);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
                System.out.println("目录创建成功: " + directoryPath.toAbsolutePath());
            }
        } catch (Exception e) {
            throw new RuntimeException("创建目录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取当前使用的目录路径（用于调试）
     */
    public String getCurrentDirectory() {
        return Paths.get(fileDirectory.replace("\\", File.separator)).toAbsolutePath().toString();
    }
}