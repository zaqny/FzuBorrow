package com.seven.fzuborrow.network.response;

import com.seven.fzuborrow.data.Good;
import com.squareup.moshi.Json;

public class FindGoodResponse {
    private int code;
    private String message;
    @Json(name = "data")
    private Good good;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }
}
