package com.rap.app.auth.service;

import com.rap.app.user.domain.model.User;
import com.rap.app.user.domain.service.UserAuthentication;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider {

	private static String JWT_HEADER_KEY = "Authorization";
	private static String BEARER_PREFIX = "key=";

	// 토큰 유효시간 (60분)
	private static final long JWT_EXPIRATION_MINUTE = 3600*1000;

	private static String JWT_SECRET = "secretkey";

	// 객체 초기화시 SecretKey를 Base64로 인코딩한다.
	@PostConstruct
	protected void init() {
		JWT_SECRET = Base64.getEncoder().encodeToString(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
	}

	// JWT 토큰 생성
	public String issueAccessToken(UserAuthentication authentication) {
		return Jwts.builder()
			.setHeaderParam("typ", "JWT")
			.setSubject((String) authentication.getPrincipal())
			.claim("uid", authentication.getUser().getUserId())
			.claim("unm", authentication.getUser().getUserNm())
			.setIssuer("SYSTEM")
			.setIssuedAt(new Date()) // 현재 시간 기반으로 생성
			.setExpiration(this.getExp()) // 만료 시간 세팅
			.signWith(SignatureAlgorithm.HS512, JWT_SECRET) // 사용할 암호화 알고리즘, signature에 들어갈 secret 값 세팅
			.compact();
	}

	public String issueRefreshToken(UserAuthentication authentication) {
		return UUID.randomUUID().toString();
	}

	/**
	 * HttpServletRequest 객체에서 JWT 추출
	 * @param request
	 * @return
	 */
	public static Optional<String> resolveToken(HttpServletRequest request) {
		String token = request.getHeader(JWT_HEADER_KEY);
		if (StringUtils.isEmpty(token) || !token.startsWith(BEARER_PREFIX)) {
			return Optional.empty();
		}
		return Optional.of(token.substring(BEARER_PREFIX.length()));
	}

	// Jwt 토큰에서 Employee 정보 추출
	public static User getEmployeeFromJWT(String token) throws MalformedJwtException {
		if (token.startsWith("key=")) {
			token = token.substring(4);
		}
		Claims claims = Jwts.parser()
			.setSigningKey(JWT_SECRET)
			.parseClaimsJws(token)
			.getBody();

		return User.builder()
			.userId(claims.getSubject())
			.userNm(claims.get("snm").toString())
			.build();
	}

	/**
	 * JWT 토큰의 유효성을 검사한다.
	 * 1. JWT 시그니처 검증
	 * 2. JWT 형식 검증
	 * 3. JWT 만료시간 검증
	 * 4. 지원하지 않는 JWT (ex. JWK, JWS 등이 아닌 케이스)
	 * 5. 특정 클레임이 비어 있는지 여부 체크
	 * @param token
	 * @return 유효성 검증 결과
	 */
	public static boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
			Jws<Claims> claims = Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
			return !claims.getBody().getExpiration().before(new Date());
		} catch (SignatureException ex) {
			log.error("Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			log.error("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			log.error("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			log.error("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty.");
		}
		return false;
	}

	private Date getExp() {
		return new Date(new Date().getTime() + JWT_EXPIRATION_MINUTE);
	}
}
