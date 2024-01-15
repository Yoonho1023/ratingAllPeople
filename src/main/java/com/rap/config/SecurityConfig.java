package com.rap.config;

import com.rap.app.auth.service.JwtAccessDeniedHandler;
import com.rap.app.auth.service.JwtAuthenticationEntryPoint;
import com.rap.app.auth.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

	// PasswordEncoder는 BCryptPasswordEncoder를 사용
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				// token을 사용하는 방식이기 때문에 csrf를 disable합니다.
				.csrf(AbstractHttpConfigurer::disable)
				.exceptionHandling((exceptionHandling) -> exceptionHandling
						.authenticationEntryPoint(jwtAuthenticationEntryPoint)
						.accessDeniedHandler(jwtAccessDeniedHandler))

				// enable h2-console
				.headers((headers) -> headers
						.frameOptions(Customizer.withDefaults()))

				// 세션을 사용하지 않기 때문에 STATELESS로 설정
				.sessionManagement((sessionManagement) -> sessionManagement
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// HttpServletRequest를 사용하는 요청들에 대한 접근제한을 설정
				.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
								.requestMatchers("/api/auth/**").permitAll() // api/auth로 시작하는 api 모두
								.anyRequest().permitAll() // TODO 일단 이렇게 작업 후 나중에 Securiy 작업!
//						.anyRequest().authenticated() // 그 외 인증 없이 접근X
				);

		return httpSecurity.build();
	}
}
