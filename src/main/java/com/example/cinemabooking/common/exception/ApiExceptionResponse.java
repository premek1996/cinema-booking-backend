package com.example.cinemabooking.common.exception;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class ApiExceptionResponse {

    @Singular
    List<String> messages;

    int status;

    @Builder.Default
    LocalDateTime timestamp = LocalDateTime.now();

}
