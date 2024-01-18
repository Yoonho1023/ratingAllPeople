package com.rap.app.user.rest.controller;

import com.rap.app.user.application.UserService;
import com.rap.app.user.rest.dto.IdDuplicateResponse;
import com.rap.app.user.rest.dto.SignInRequest;
import com.rap.config.exception.DuplicateIdException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/user", "v1/api/user"})
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     * TODO 단계 분리 필요? or 단일 설정 필요
     */
    @PostMapping("signIn")
    public String signIn(@RequestBody SignInRequest request) {
        return userService.signIn(request);
    }

    @GetMapping("id-duplicate/{id}")
    public ResponseEntity<IdDuplicateResponse> idDuplicate(@PathVariable("id") String id) throws DuplicateIdException {
        IdDuplicateResponse response = userService.idDuplicate(id);
        return ResponseEntity.ok(response);
    }



    // TODO 비밀번호 찾기
    // TODO ID 중복 체크
    // TODO 전화번호 인증 / 주민번호로 인증?

}
