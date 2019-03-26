package io.fourfinanceit.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.hateoas.ResourceSupport;

import java.io.Serializable;

public class Home extends ResourceSupport implements Serializable {

    private final String message;

    @JsonCreator
    public Home() {
        this.message = "Welcome to Micro lend API";
    }

    public String getMessage() {
        return message;
    }
}
