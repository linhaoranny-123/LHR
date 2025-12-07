package com.lhr.filetransfer.exception;

import com.lhr.filetransfer.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
/**
 * @author lhr
 * @additional_information
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<String>> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("文件大小超过限制(1GB)"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneralException(Exception exc) {
        return ResponseEntity.internalServerError()
                .body(ApiResponse.error("服务器内部错误: " + exc.getMessage()));
    }
}