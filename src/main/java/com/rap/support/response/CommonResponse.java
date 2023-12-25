package com.rap.support.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {

    private int status;

    private T result;

    /**
     * @description API에서 공통으로 사용하는 응답 객체
     * @param httpStatus: httpStatus 값
     * @param result: 응답에 대한 성공 or 실패 코드값 및 return 하는 Json Body 객체들로 이루어진 값
     */
    public CommonResponse(HttpStatus httpStatus, T result) {
        this.status = httpStatus.value();
        this.result = result;
    }

    public CommonResponse setResponse(HttpStatus httpStatus, T result) {
        return new CommonResponse(httpStatus, result);
    }

}
