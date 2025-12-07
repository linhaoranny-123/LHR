package com.lhr.filetransfer.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
            @RequestParam(required = false) String token,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        // 移除了 HttpServletResponse 参数

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

            // 确定内容类型
            String contentType = determineContentType(filePath);

            // 正确编码文件名
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replace("+", "%20")
                    .replaceAll("%(?![0-9a-fA-F]{2})", "%25") // 转义 % 符号
                    .replaceAll("\\+", "%20"); // 确保空格是 %20

            // 构建 Content-Disposition 头
            // 使用 RFC 5987 标准格式，支持 UTF-8 文件名
            String contentDisposition = String.format("attachment; filename*=UTF-8''%s", encodedFileName);

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(Files.size(filePath)));

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