package com.skg.smodel.core.exception.auth;


import com.skg.smodel.core.constant.CommonConstants;
import com.skg.smodel.core.exception.BaseException;

public class ClientForbiddenException extends BaseException {
    public ClientForbiddenException(String message) {
        super(message, CommonConstants.EX_CLIENT_FORBIDDEN_CODE);
    }

}
