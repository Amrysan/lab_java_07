package com.example.labspring1.service;

public class RequestCounter {
    private long count;

    public RequestCounter() {
        this.count = 0;
    }

    public synchronized void increment() {
        count++;
    }

    public synchronized long getCount() {
        return count;
    }

    public synchronized void reset() {
        count = 0;
    }
}