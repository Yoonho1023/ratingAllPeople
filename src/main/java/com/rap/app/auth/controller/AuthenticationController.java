package com.rap.app.auth.controller;

import com.rap.app.auth.entity.Token;
import com.rap.app.auth.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping({"/api/auth/", "v1/api/auth/"})
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	/**
	 * 로그인 - id와 Secret으로 인증
	 */
	@PostMapping("login")
	public ResponseEntity login(@RequestBody Token.Request request, HttpServletResponse response) {
		Token.Response tokenResponse = authenticationService.login(request.getLoginId(), request.getSecret());

		// create a cookie
		response.addCookie(createCookie(tokenResponse));
		return ResponseEntity.ok(tokenResponse);
	}

	/**
	 * 리프레시 토큰을 이용하여 새로운 AccessToken을 발급
	 */
	@PostMapping("refresh")
	public ResponseEntity refresh(@RequestBody Token.Refresh request) {
		Token.Response response = authenticationService.refresh(request.getRefreshToken());
		return ResponseEntity.ok(response);
	}

	/**
	 * 해당 리프레시 토큰에 대해 로그아웃 처리
	 * @param request
	 * @return
	 */
	@PostMapping("logout")
	public ResponseEntity logout(@RequestBody Token.Refresh request) {
		authenticationService.logout(request.getRefreshToken());
		return new ResponseEntity(HttpStatus.OK);
	}

	private Cookie createCookie(Token.Response tokenResponse) {
		Cookie cookie = new Cookie("refresh_token", tokenResponse.getRefreshToken());

		// expires in 30 days
		cookie.setMaxAge(30 * 24 * 60 * 60);

		// optional properties
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		return cookie;
	}

}
