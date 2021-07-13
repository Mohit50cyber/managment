package com.moglix.wms.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse extends BaseResponse {
    private Object data;

    public ApiResponse(String message, boolean status, int code, Object data) {
        this.setMessage(message);
        this.setStatus(status);
        this.setCode(code);
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
