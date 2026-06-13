package com.innowise.orderservice.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorDetails {
    private int statusCode;
    private String message;
    private String details;
}
