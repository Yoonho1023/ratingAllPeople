package com.rap.app.user.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
public class SignInRequest {

    private String userId;
    private String pw; // bcrypt 도전 해보기
    private String userNm;
    private String mobileNo;

}
