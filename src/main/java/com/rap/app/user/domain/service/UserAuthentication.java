package com.rap.app.user.domain.service;

import com.rap.app.user.domain.model.User;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class UserAuthentication extends UsernamePasswordAuthenticationToken {

	@Getter
	private User user;

	public UserAuthentication(User emp, String credentials, List<GrantedAuthority> authorities) {
		super(emp.getUserId(), credentials, authorities);
		this.user = emp;
	}
}

