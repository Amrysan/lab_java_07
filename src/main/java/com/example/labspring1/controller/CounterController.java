package com.example.labspring1.controller;

import com.example.labspring1.service.RequestCounter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/counter")
public class CounterController {

    private final RequestCounter requestCounter;

    public CounterController(RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
    }

    @GetMapping
    public long getRequestCount() {
        return requestCounter.getCount();
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> resetRequestCount() {
        requestCounter.reset();
        return ResponseEntity.noContent().build();
    }
}