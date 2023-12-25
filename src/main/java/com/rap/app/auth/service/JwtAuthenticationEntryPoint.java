package com.rap.app.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws
		IOException,
		ServletException {
		log.error("Responding with unauthorized error. Message - {}", e.getMessage());

		// 유효한 자격증명을 제공하지 않고 접근하려 할때 401(인증 실패)
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}
}
