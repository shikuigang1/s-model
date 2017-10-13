package com.skg.smodel.core.exception.auth;


import com.skg.smodel.core.constant.CommonConstants;
import com.skg.smodel.core.exception.BaseException;

public class TokenErrorException extends BaseException {
    public TokenErrorException(String message, int status) {
        super(message, CommonConstants.EX_TOKEN_ERROR_CODE);
    }
}
