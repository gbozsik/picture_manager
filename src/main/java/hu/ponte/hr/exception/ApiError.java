package hu.ponte.hr.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

import java.time.Instant;

@ToString
public class ApiError {
    @JsonProperty("code") String code;
    @JsonProperty("timeStamp")
    java.time.Instant timeStamp;
    @JsonProperty("message") String message;

    public ApiError(String code, Instant timeStamp, String message) {
        this.code = code;
        this.timeStamp = timeStamp;
        this.message = message;
    }
}
