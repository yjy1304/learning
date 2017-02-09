package com.gemyoung;

/**
 * Created by weilong on 2017/2/9.
 */
public class CommonException extends RuntimeException{
    private static final long serialVersionUID = 5665628883016025865L;
    private String errorCode;
    private String errorMsg;

    public CommonException(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getMessage() {
        return this.errorMsg + "_" + super.getMessage();
    }
}
