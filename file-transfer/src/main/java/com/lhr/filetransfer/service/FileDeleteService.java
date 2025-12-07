package com.lhr.filetransfer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author lhr
 * @additional_information
 * 手动删除文件
 */
@Service
public class FileDeleteService {

    @Value("${app.file.uploadDir}")
    private String fileDirectory;

    /**
     * 删除文件
     */
    public boolean deleteFile(String fileName) {
        try {
            // 安全检查：防止路径遍历攻击
            if (fileName == null || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
                throw new SecurityException("非法文件名: " + fileName);
            }

            Path filePath = Paths.get(fileDirectory, fileName);
            File file = filePath.toFile();

            // 检查文件是否存在
            if (!file.exists()) {
                throw new RuntimeException("文件不存在: " + fileName);
            }

            // 检查是否是文件（防止删除目录）
            if (!file.isFile()) {
                throw new RuntimeException("指定的路径不是文件: " + fileName);
            }

            // 删除文件
            boolean deleted = Files.deleteIfExists(filePath);

            if (!deleted) {
                throw new RuntimeException("文件删除失败: " + fileName);
            }

            return true;

        } catch (SecurityException e) {
            throw new SecurityException("安全错误: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("删除文件时发生IO错误: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("删除文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查文件是否存在
     */
    public boolean fileExists(String fileName) {
        try {
            Path filePath = Paths.get(fileDirectory, fileName);
            File file = filePath.toFile();
            return file.exists() && file.isFile();
        } catch (Exception e) {
            return false;
        }
    }

}
