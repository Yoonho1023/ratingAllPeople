package com.rap.config.exception;

import com.rap.support.messages.PreparedMessages;
import com.rap.support.response.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionAdvisor {

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ValidIdException.class})
    public CommonResponse processValidationError(ValidIdException e) {
        CommonResponse response = new CommonResponse<>();
        PreparedMessages preparedMessages = e.getPreparedMessages();

        return response.setResponse(HttpStatus.BAD_REQUEST, preparedMessages.getMessage());
    }
}
