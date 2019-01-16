package ru.varamila.medos.atoll;

public class BaseResponse {

    private final String status;
    private final Integer code;
    public BaseResponse(String aStatus, Integer aCode) {
        status = aStatus;
        code = aCode;
    }
}
