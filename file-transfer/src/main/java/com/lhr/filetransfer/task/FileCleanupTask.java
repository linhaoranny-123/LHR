package com.lhr.filetransfer.task;

import com.lhr.filetransfer.service.FileStorageService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
/**
 * @author lhr
 * @additional_information
 */

@Component
public class FileCleanupTask {

    private final FileStorageService fileStorageService;

    public FileCleanupTask(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Scheduled(cron = "0 0 4 * * ?") // 每天凌晨4点执行
    public void cleanupExpiredFiles() {
        fileStorageService.cleanupExpiredFiles();
    }
}