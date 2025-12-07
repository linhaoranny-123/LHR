package com.lhr.filetransfer.controller;

import com.lhr.filetransfer.dto.ApiResponse;
import com.lhr.filetransfer.service.FileDeleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
/**
 * @author lhr
 * @additional_information
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // 允许跨域访问
public class FileDeleteController {
    @Autowired
    private FileDeleteService fileService;
    /**
     * 删除文件
     */
    @DeleteMapping("/delete")
    public ApiResponse<String> deleteFile(
            @RequestParam String fileName,
            @RequestHeader(value = "Authorization", required = false) String token) {

        try {
            // 验证token（这里可以根据您的需求实现具体的token验证逻辑）
            if (token == null || token.trim().isEmpty()) {
                return ApiResponse.error("未提供身份验证Token");
            }

            // 这里可以添加更严格的token验证逻辑
//            if (!isValidToken(token)) {
//                return ApiResponse.error("身份验证失败");
//            }

            // 检查文件是否存在
            if (!fileService.fileExists(fileName)) {
                return ApiResponse.error("文件不存在: " + fileName);
            }

            // 删除文件
            boolean deleted = fileService.deleteFile(fileName);

            if (deleted) {
                return ApiResponse.success("文件删除成功", fileName);
            } else {
                return ApiResponse.error("文件删除失败");
            }

        } catch (SecurityException e) {
            return ApiResponse.error("安全错误: " + e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("删除文件失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除文件
     */
    @DeleteMapping("/deleteBatch")
    public ApiResponse<List<String>> deleteFiles(
            @RequestBody List<String> fileNames,
            @RequestHeader(value = "Authorization", required = false) String token) {

        try {
            // 验证token
            if (token == null || token.trim().isEmpty()) {
                return ApiResponse.error("未提供身份验证Token");
            }

//            if (!isValidToken(token)) {
//                return ApiResponse.error("身份验证失败");
//            }

            if (fileNames == null || fileNames.isEmpty()) {
                return ApiResponse.error("未提供要删除的文件名列表");
            }

            List<String> deletedFiles = new ArrayList<>();
            List<String> failedFiles = new ArrayList<>();

            for (String fileName : fileNames) {
                try {
                    if (fileService.fileExists(fileName)) {
                        boolean deleted = fileService.deleteFile(fileName);
                        if (deleted) {
                            deletedFiles.add(fileName);
                        } else {
                            failedFiles.add(fileName + " (删除失败)");
                        }
                    } else {
                        failedFiles.add(fileName + " (文件不存在)");
                    }
                } catch (Exception e) {
                    failedFiles.add(fileName + " (" + e.getMessage() + ")");
                }
            }

            if (failedFiles.isEmpty()) {
                return ApiResponse.success("所有文件删除成功", deletedFiles);
            } else {
                String message = "成功删除 " + deletedFiles.size() + " 个文件，" +
                        failedFiles.size() + " 个文件失败";
                return new ApiResponse<>(true, message, deletedFiles);
            }

        } catch (Exception e) {
            return ApiResponse.error("批量删除文件失败: " + e.getMessage());
        }
    }
}
