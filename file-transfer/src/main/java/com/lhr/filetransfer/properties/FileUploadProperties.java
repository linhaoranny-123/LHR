package com.lhr.filetransfer.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lhr
 * @additional_information
 */
@Component
@ConfigurationProperties(prefix = "app.file")
public class FileUploadProperties {
    private String uploadDir;
    private String tokenSecret;


    // getters and setters
    public String getUploadDir() { return uploadDir; }
    public void setUploadDir(String uploadDir) { this.uploadDir = uploadDir; }
    public String getTokenSecret() { return tokenSecret; }
    public void setTokenSecret(String tokenSecret) { this.tokenSecret = tokenSecret; }
}