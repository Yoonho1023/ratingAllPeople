package com.rap.app.user.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IdDuplicateResponse {

    @JsonProperty("message")
    private String message;

}
