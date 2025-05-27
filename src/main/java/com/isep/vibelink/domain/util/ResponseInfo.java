package com.isep.vibelink.domain.util;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseInfo {
    private String msg;
    private Boolean ok;
    private Object data;

    public ResponseInfo(String msg, Boolean ok, Object data) {
        this.msg = msg;
        this.ok = ok;
        this.data = data;
    }

}
