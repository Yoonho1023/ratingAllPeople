package com.rap.support.messages;

import lombok.Getter;

@Getter
public enum PreparedMessages {
    LOGIN_INFO_INVALID("아이디 또는 비밀번호가 올바르지 않습니다.", MessageLevel.WARN);

    private String message;
    private MessageLevel level;

    PreparedMessages(String message, MessageLevel level) {
        this.message = message;
        this.level = level;
    }
}
