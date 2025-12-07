package com.lhr.filetransfer.controller;

import com.lhr.filetransfer.dto.ApiResponse;
import com.lhr.filetransfer.dto.FileInfo;
import com.lhr.filetransfer.service.FileListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
/**
 * @author lhr
 * @additional_information
 */
@RestController
@RequestMapping("/api")
//@CrossOrigin(origins = "*") // 允许跨域访问
public class FileListController {
    @Autowired
    private FileListService fileListService;

    /**
     * 获取文件列表
     */
    @GetMapping("/fileList")
    public ApiResponse<List<FileInfo>> getFileList() {
        try {
            // 确保目录存在
            //fileListService.ensureDirectoryExists();

            // 获取文件列表
            List<FileInfo> fileList = fileListService.getFileList();

            return ApiResponse.success("获取文件列表成功", fileList);

        } catch (Exception e) {
            return ApiResponse.error("获取文件列表失败: " + e.getMessage());
        }
    }
}
