package com.rap.app.user.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
public class SignInRequest {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("pwd")
    private String pwd; // bcrypt 도전 해보기

    @JsonProperty("userNm")
    private String userNm;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("mobileNo")
    private String mobileNo;

}
