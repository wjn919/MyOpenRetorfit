package cn.com.argorse.common.exception;

public class ApiException extends RuntimeException {
    private int errorCode;
    private String extendErrorMsg;

    public ApiException(int code, String msg) {
        super(msg);
        this.errorCode = code;
    }


    public ApiException(int code, String msg, String extendErrorMsg) {
        super(msg);
        this.errorCode = code;
        this.extendErrorMsg = extendErrorMsg;
    }


    public int getErrorCode() {
        return errorCode;
    }

    public boolean isTokenExpried() {
        return errorCode == ApiCode.ERROR_USER_AUTHORIZED;
    }

    public boolean isInvlidClient() {
        return errorCode == ApiCode.ERROR_CLIENT_AUTHORIZED;
    }


    public String getExtendErrorMsg() {
        return extendErrorMsg;
    }
}
