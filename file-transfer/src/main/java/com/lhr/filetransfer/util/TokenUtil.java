package com.lhr.filetransfer.util;

import org.springframework.stereotype.Component;
import com.lhr.filetransfer.properties.FileUploadProperties;

/**
 * @author lhr
 * @additional_information
 */
@Component

public class TokenUtil {

    private final String validToken;

    public TokenUtil(FileUploadProperties properties) {
        this.validToken = properties.getTokenSecret();
    }

    public boolean validateToken(String token) {
        return validToken.equals(token);
    }
}