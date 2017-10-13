package com.skg.smodel.core.msg.msg.auth;

import com.skg.smodel.core.constant.RestCodeConstants;
import com.skg.smodel.core.msg.BaseResponse;

public class TokenForbiddenResponse  extends BaseResponse {
    public TokenForbiddenResponse(String message) {
        super(RestCodeConstants.TOKEN_FORBIDDEN_CODE, message);
    }
}
