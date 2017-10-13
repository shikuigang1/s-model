package com.skg.smodel.core.exception.auth;


import com.skg.smodel.core.constant.CommonConstants;
import com.skg.smodel.core.exception.BaseException;

public class ClientInvalidException extends BaseException {
    public ClientInvalidException(String message) {
        super(message, CommonConstants.EX_CLIENT_INVALID_CODE);
    }
}
