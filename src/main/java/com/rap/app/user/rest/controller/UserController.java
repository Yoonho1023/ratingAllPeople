package com.rap.app.user.rest.controller;

import com.rap.app.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/auth/", "v1/api/auth/"})
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     * TODO 단계 분리 필요? or 단일 설정 필요
     */
    @PostMapping("signIn")
    public void signIn(@RequestBody String request) {
        userService.signIn(request);

    }

    // TODO 비밀번호 찾기
    // TODO ID 중복 체크
    // TODO 전화번호 인증 / 주민번호로 인증?

}
