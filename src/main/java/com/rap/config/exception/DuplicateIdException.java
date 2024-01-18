package com.rap.config.exception;

import com.rap.support.messages.PreparedMessages;
import lombok.Getter;

public class DuplicateIdException extends RuntimeException {

    @Getter
    private PreparedMessages preparedMessages;

    public DuplicateIdException(PreparedMessages message) {
        this.preparedMessages = message;
    }
}
