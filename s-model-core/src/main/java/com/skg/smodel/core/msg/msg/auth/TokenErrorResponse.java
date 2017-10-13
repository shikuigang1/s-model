package com.skg.smodel.core.msg.msg.auth;


import com.skg.smodel.core.constant.RestCodeConstants;
import com.skg.smodel.core.msg.BaseResponse;

public class TokenErrorResponse extends BaseResponse {
    public TokenErrorResponse(String message) {
        super(RestCodeConstants.TOKEN_ERROR_CODE, message);
    }
}
