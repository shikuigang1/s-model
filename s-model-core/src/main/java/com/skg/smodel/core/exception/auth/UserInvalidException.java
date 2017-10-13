package com.skg.smodel.core.exception.auth;


import com.skg.smodel.core.constant.CommonConstants;
import com.skg.smodel.core.exception.BaseException;

public class UserInvalidException extends BaseException {
    public UserInvalidException(String message) {
        super(message, CommonConstants.EX_USER_INVALID_CODE);
    }
}
