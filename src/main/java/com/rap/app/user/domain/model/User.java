package com.rap.app.user.domain.model;

import com.rap.support.jpa.AuditEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
public class User extends AuditEntity {

	@Id
	@Column(name = "user_id")
	@Schema(description = "사용자 Id")
	private String userId;

	@Column(name = "user_pwd")
	@Schema(description = "사용자 비밀번호")
	private String pwd;

	@Column(name = "user_nm")
	@Schema(description = "성명")
	private String userNm;

	@Column(name = "mobile_no")
	@Schema(description = "휴대폰 번호")
	private String mobileNo;

	@Column(name = "pwd_fail_cnt")
	@Schema(description = "비밀번호 실패 횟수")
	private int pwdErrorCnt;

	@Column(name = "pwd_chg_dt")
	@Schema(description = "비밀번호 변경 일자")
	private String pwdChangeDt;

	@Column(name = "old_pwd")
	@Schema(description = "예전비밀번호")
	private String beforePwd;
}
