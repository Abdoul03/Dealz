package com.doul.dealz.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ApiErreur {
    private int status;
    private String error;
    private String message;
    private List<String> details;

    public ApiErreur(HttpStatus status, String message, List<String> details) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.details = details;
    }

    public ApiErreur(HttpStatus status, String message) {
        this(status, message, List.of());
    }
}
