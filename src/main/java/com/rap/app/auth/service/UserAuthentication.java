package com.rap.app.auth.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class UserAuthentication extends UsernamePasswordAuthenticationToken {

    @Getter
    private UserJwt userJwt;

    public UserAuthentication(UserJwt usr, String credentials) {
        super(usr.getUserId(), credentials);
        userJwt = usr;
    }

    public UserAuthentication(UserJwt usr, String credentials, List<GrantedAuthority> authorities) {
        super(usr.getUserId(), credentials, authorities);
        userJwt = usr;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserJwt {
        @JsonProperty("user_id")
        private String userId;

        @JsonProperty("user_nm")
        private String userNm;

        @JsonProperty("nickname")
        private String nickname;
    }
}
