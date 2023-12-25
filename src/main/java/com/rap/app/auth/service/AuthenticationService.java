package com.rap.app.auth.service;

import com.rap.app.auth.entity.Token;
import com.rap.app.user.domain.model.User;
import com.rap.app.user.domain.repository.UserRepository;
import com.rap.app.user.domain.service.UserAuthentication;
import com.rap.config.exception.InvalidRefreshTokenException;
import com.rap.support.messages.PreparedMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {

	private final UserRepository userRepository;
	private final JwtTokenProvider tokenProvider;
	private final RedisTemplate<String, String> redisTemplate;

	@Value("${redis.config.refresh.prefix:refresh}")
	private String REFRESH_TOKEN_KEY;

	@Value("${redis.config.user.prefix:user_refresh}")
	private String USER_REFRESH_TOKEN_KEY;

	@Value("${redis.config.refresh.ttl:8640000}") // 100 days
	private long ttl;

	/**
	 * Do not use this method. Interface 구현 자체가 목적
	 *
	 * @param username
	 * @return UserDetails (사용하지 않음)
	 * @throws UsernameNotFoundException
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return null;
	}

	/**
	 * UserId와 Password를 이용하여 로그인한다. 로그인이 성공하면 AccessToken / RefreshToken이 발급된다.
	 */
	public Token.Response login(String userId, String password) {
		AtomicReference<Token.Response> result = new AtomicReference<>();

		// select user data from database
		userRepository.findById(userId).ifPresentOrElse(user -> {
			if (!isSHA512PasswordEquals(user, password)) {
				log.info("Pwd mismatch: {}", userId);
				throw new IllegalArgumentException(PreparedMessages.LOGIN_INFO_INVALID.getMessage());
			}

			// 기존에 저장된 리프레시 토큰이 있다면 삭제한다.
			this.getRefreshTokenByUserId(userId).ifPresent(token -> {
				this.volatilize(refreshKey(token));
			});

			// 엑세스 토큰과 리프레시 토큰을 발급한다.
			UserAuthentication authentication = createAuthentication(user);
			String accessToken = tokenProvider.issueAccessToken(authentication);
			String refreshToken = tokenProvider.issueRefreshToken(authentication);

			// (리프레시 토큰 => UserId) 형식으로 데이터를 레디스에 저장한다. 키 형식은 'refresh::<리프레시_토큰>'이다.
			this.persist(refreshKey(refreshToken), user.getUserId(), true);

			// (UserId => 리프레시 토큰) 형식으로 데이터를 레디스에 저장한다.
			// UserId 기반으로 리프레시 토큰을 찾을 수 있도록 하여 로그인시 기존 토큰을 삭제하기 위함이다.
			this.persist(userRefreshKey(user.getUserId()), refreshToken, false);

			// 응답 결과를 세팅한다
			result.set(Token.Response.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build());
		}, () -> {
			throw new IllegalArgumentException(PreparedMessages.LOGIN_INFO_INVALID.getMessage());
		});
		return result.get();
	}

	/**
	 * RefreshToken을 이용하여 새로운 AccessToken을 받아온다
	 *
	 * @param refreshToken 리프레시 토큰
	 * @return 새로 생성한 AccessToken & 기존의 RefreshToken
	 */
	public Token.Response refresh(String refreshToken) {
		// 레디스에 저장된 리프레시 토큰을 불러온다.
		ValueOperations<String, String> redisOps = redisTemplate.opsForValue();

		String userId = redisOps.getAndExpire(refreshKey(refreshToken), ttl, TimeUnit.SECONDS);
		if (userId == null) {
			throw new InvalidRefreshTokenException();
		}

		// 리프레시 토큰과 함께 저장된 EmpNo를 DB에서 조회하여 유저 정보를 가져온다
		AtomicReference<Token.Response> result = new AtomicReference<>();
		userRepository.findById(userId).ifPresentOrElse(user -> {
			// 엑세스 토큰과 리프레시 토큰을 발급한다.
			UserAuthentication authentication = createAuthentication(user);
			String accessToken = tokenProvider.issueAccessToken(authentication);

			result.set(Token.Response.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build());
		}, () -> new IllegalArgumentException("존재하지 않는 사용자 ID입니다"));

		return result.get();
	}

	/**
	 * 리프레시 토큰의 영속성을 해제한다. 즉, 레디스에 저장된 리프레시 토큰을 삭제한다
	 */
	public Boolean logout(String refreshToken) {

		ValueOperations<String, String> redisOps = redisTemplate.opsForValue();

		// 리프레시 토큰을 이용하여 userId를 불러온다. user_refresh::XXX 형식의 데이터를 함께 삭제하기 위해서이다
		String key = refreshKey(refreshToken);
		String userId = redisOps.get(key);
		if (userId == null) {
			return false;
		}

		// 레디스에 저장된 리프레시 토큰을 삭제한다
		return this.volatilize(userRefreshKey(userId)) && this.volatilize(refreshKey(refreshToken));
	}

	/**
	 * 레디스에 데이터를 영속화한다( = 저장한다는 뜻)
	 *
	 * @param key   저장할 키
	 * @param value 저장할 값
	 */
	private boolean persist(String key, String value, boolean expired) {
		ValueOperations<String, String> redisOps = redisTemplate.opsForValue();
		if (expired) {
			return redisOps.setIfAbsent(key, value, ttl, TimeUnit.SECONDS);
		} else {
			redisOps.set(key, value);
			return true;
		}
	}

	/**
	 * 레디스에 영속화된 데이터를 휘발시킨다( = 삭제한다는 뜻)
	 *
	 * @param key 삭제할 키
	 * @return 삭제 결과
	 */
	private boolean volatilize(String key) {
		return redisTemplate.opsForValue().getOperations().delete(key);
	}

	private String refreshKey(String refreshToken) {
		if (refreshToken.startsWith(REFRESH_TOKEN_KEY)) {
			return refreshToken;
		} else {
			return String.format("%s::%s", REFRESH_TOKEN_KEY, refreshToken);
		}
	}

	/**
	 * 인증 토큰을 발급하기 위해 필요한 Authentication 객체를 생성한다
	 */
	public UserAuthentication createAuthentication(User user) {
		User emp = User.builder()
			.userId(user.getUserId())
			.userNm(user.getUserNm()).build();
		return new UserAuthentication(emp, null, null);
	}

	/**
	 * 암호화 된 패스워드를 비교합니다
	 *
	 * @param user
	 * @param use512Password
	 * @return
	 */
	private boolean isSHA512PasswordEquals(User user, String use512Password) {
		return user.getPwd().equals(use512Password);
	}

	/**
	 * UserId를 기반으로 저장되어 있는 RefreshToken 정보를 가져온다
	 */
	private Optional<String> getRefreshTokenByUserId(String userId) {
		ValueOperations<String, String> redisOps = redisTemplate.opsForValue();
		String refreshToken = redisOps.get(userRefreshKey(userId));
		return Optional.ofNullable(refreshToken);
	}

	private String userRefreshKey(String userId) {
		if (userId.startsWith(USER_REFRESH_TOKEN_KEY)) {
			return userId;
		} else {
			return String.format("%s::%s", USER_REFRESH_TOKEN_KEY, userId);
		}
	}
}
