package com.lhr.filetransfer.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * @author lhr
 * @additional_information
 * 下载接口
 */
@RestController
@RequestMapping("/api")
public class FileDownloadController {

    @Value("${app.file.uploadDir}")
    private String fileDirectory;

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam String fileName,
            @RequestParam(required = false) String token, // URL参数方式
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        // Token验证（支持URL参数和Header两种方式）
        String actualToken = token != null ? token : authHeader;
//        if (actualToken == null || !validTokens.containsKey(actualToken)) {
//            return ResponseEntity.status(401).body(null);
//        }

        try {
            Path filePath = Paths.get(fileDirectory).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // 安全检查
//            if (!isSafePath(filePath)) {
//                return ResponseEntity.badRequest().build();
//            }

            // 确定内容类型
            String contentType = determineContentType(filePath);

            // 设置响应头，强制下载
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private String determineContentType(Path filePath) throws IOException {
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return contentType;
    }
}