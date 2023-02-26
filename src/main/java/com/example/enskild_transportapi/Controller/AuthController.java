package com.example.enskild_transportapi.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/auth/*")

public class AuthController {

    @GetMapping("getToken/{username}")
    public ResponseEntity<List<Object>> getToken(@PathVariable String username) {

        String originalInput = username;
        String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
        return ResponseEntity
                .status(200)
                .header("x-access-token", encodedString)
                .build();
    }
}
