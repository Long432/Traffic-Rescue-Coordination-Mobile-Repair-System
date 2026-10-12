package com.sosrescuenow.presentation.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/** Dinh dang loi thong nhat cho moi API. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(int status, String maLoi, String thongBao, Map<String, String> chiTiet) {

    public static ApiError of(int status, String maLoi, String thongBao) {
        return new ApiError(status, maLoi, thongBao, null);
    }

    public static ApiError of(int status, String maLoi, String thongBao, Map<String, String> chiTiet) {
        return new ApiError(status, maLoi, thongBao, chiTiet);
    }
}
