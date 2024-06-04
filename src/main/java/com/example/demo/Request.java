package com.example.demo;

// Represents a request for an elevator pickup
public class Request {
    int floor;
    int direction;

    Request(int floor, int direction) {
        this.floor = floor;
        this.direction = direction;
    }
}