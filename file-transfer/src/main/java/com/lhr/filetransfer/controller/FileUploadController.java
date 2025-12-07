package com.lhr.filetransfer.controller;

import com.lhr.filetransfer.dto.ApiResponse;
import com.lhr.filetransfer.service.FileStorageService;
import com.lhr.filetransfer.util.TokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author lhr
 * @additional_information
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final TokenUtil tokenUtil;
    private final FileStorageService fileStorageService;

    public FileUploadController(TokenUtil tokenUtil, FileStorageService fileStorageService) {
        this.tokenUtil = tokenUtil;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("token") String token) {

        try {
            // 校验token
            if (!tokenUtil.validateToken(token)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("身份验证失败"));
            }

            // 校验文件大小 (1GB = 1073741824 bytes)
            if (file.getSize() > 1073741824L) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("文件大小不能超过1GB"));
            }

            // 校验文件是否为空
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("文件不能为空"));
            }

            // 存储文件
            String fileName = fileStorageService.storeFile(file);

            return ResponseEntity.ok()
                    .body(ApiResponse.success("文件上传成功", fileName));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("文件上传失败: " + e.getMessage()));
        }
    }
}