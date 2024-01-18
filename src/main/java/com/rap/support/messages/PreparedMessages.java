package com.rap.support.messages;

import lombok.Getter;

@Getter
public enum PreparedMessages {
    LOGIN_INFO_INVALID("아이디 또는 비밀번호가 올바르지 않습니다.", MessageLevel.WARN),
    DUPLICATE_ID("해당 아이디는 이미 사용중입니다.", MessageLevel.INFO),
    ALLOW_TO_USE_THIS_ID("해당 아이디는 사용 가능합니다.", MessageLevel.INFO),
    ;

    private String message;
    private MessageLevel level;

    PreparedMessages(String message, MessageLevel level) {
        this.message = message;
        this.level = level;
    }
    }
