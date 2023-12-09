package com.rap.app.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import jakarta.validation.constraints.NotNull;

public class Token {

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static final class Request {
		@JsonProperty("login_id")
		private String loginId;

		@JsonProperty("secret")
		private String secret;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static final class Refresh {
		@NotNull
		@JsonProperty("refresh_token")
		private String refreshToken;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static final class Response {
		@JsonProperty("access_token")
		private String accessToken;

		@JsonProperty("refresh_token")
		private String refreshToken;
	}
}
