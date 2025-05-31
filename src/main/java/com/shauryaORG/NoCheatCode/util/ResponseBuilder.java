package com.shauryaORG.NoCheatCode.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseBuilder {
    public static <T> ResponseEntity<T> success(T body) {
        return ResponseEntity.ok(body);
    }

    public static <T> ResponseEntity<T> error(T body, HttpStatus status) {
        return ResponseEntity.status(status).body(body);
    }
}

