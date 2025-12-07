package com.lhr.filetransfer.service;

import com.lhr.filetransfer.properties.FileUploadProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author lhr
 * @additional_information
 */

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public FileStorageService(FileUploadProperties properties) {
        this.fileStorageLocation = Paths.get(properties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("无法创建文件存储目录", ex);
        }
    }

    public String storeFile(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileName = generateUniqueFileName(originalFileName);

        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation);

        // 安排7天后删除文件
        scheduleFileDeletion(targetLocation, 7, TimeUnit.DAYS);

        return fileName;
    }

    private String generateUniqueFileName(String originalFileName) {
        String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));

        String fileName = originalFileName;
        int counter = 1;

        while (Files.exists(fileStorageLocation.resolve(fileName))) {
            fileName = baseName + "(" + counter + ")" + extension;
            counter++;
        }

        return fileName;
    }

    private void scheduleFileDeletion(Path filePath, long delay, TimeUnit unit) {
        scheduler.schedule(() -> {
            try {
                Files.deleteIfExists(filePath);
                System.out.println("文件已自动删除: " + filePath.getFileName());
            } catch (IOException e) {
                System.err.println("删除文件失败: " + filePath.getFileName() + ", 错误: " + e.getMessage());
            }
        }, delay, unit);
    }

    public void cleanupExpiredFiles() {
        try {
            Files.list(fileStorageLocation)
                    .filter(path -> {
                        try {
                            LocalDateTime fileTime = Files.getLastModifiedTime(path).toInstant()
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toLocalDateTime();
                            return ChronoUnit.DAYS.between(fileTime, LocalDateTime.now()) >= 7;
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                            System.out.println("清理过期文件: " + path.getFileName());
                        } catch (IOException e) {
                            System.err.println("清理文件失败: " + path.getFileName());
                        }
                    });
        } catch (IOException e) {
            System.err.println("清理过期文件时出错: " + e.getMessage());
        }
    }
}