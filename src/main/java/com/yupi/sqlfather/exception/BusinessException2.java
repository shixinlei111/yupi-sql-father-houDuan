package com.yupi.sqlfather.exception;


import com.yupi.sqlfather.common.ErrorCode;

public class BusinessException2 extends RuntimeException{


    private final int code;

    public BusinessException2(String message, int code) {
        super(message);
        this.code = code;
    }

    public BusinessException2(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
