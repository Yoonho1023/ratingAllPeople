package com.rap.app.auth.entity;

import com.rap.app.user.domain.model.User;
import com.rap.app.user.domain.service.UserAuthentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuthenticationContext {

	public static Optional<User> getCurrentEmployee() {
		SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
		if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
			return Optional.empty();
		}
		final UserAuthentication authentication = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return Optional.empty();
		}

		User user = authentication.getUser();
		return Optional.ofNullable(user);
	}
}
