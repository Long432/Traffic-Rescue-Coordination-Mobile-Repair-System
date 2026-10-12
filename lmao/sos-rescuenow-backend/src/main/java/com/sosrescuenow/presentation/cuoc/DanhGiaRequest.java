package com.sosrescuenow.presentation.cuoc;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DanhGiaRequest(
        @NotNull(message = "Số sao không được để trống")
        @Min(value = 1, message = "Số sao từ 1 đến 5")
        @Max(value = 5, message = "Số sao từ 1 đến 5")
        Integer soSao,

        @Size(max = 500, message = "Nhận xét tối đa 500 ký tự")
        String nhanXet) {
}
