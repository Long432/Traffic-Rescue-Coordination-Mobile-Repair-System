package com.sosrescuenow.presentation.cuoc;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GuiSosRequest(
        @NotNull(message = "Loại sự cố không được để trống")
        Integer loaiSuCoId,

        @NotNull(message = "Vĩ độ không được để trống")
        @DecimalMin(value = "-90.0", message = "Vĩ độ phải từ -90 đến 90")
        @DecimalMax(value = "90.0", message = "Vĩ độ phải từ -90 đến 90")
        Double latitude,

        @NotNull(message = "Kinh độ không được để trống")
        @DecimalMin(value = "-180.0", message = "Kinh độ phải từ -180 đến 180")
        @DecimalMax(value = "180.0", message = "Kinh độ phải từ -180 đến 180")
        Double longitude,

        @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
        String diaChi,

        @Size(max = 500, message = "Ghi chú tối đa 500 ký tự")
        String ghiChu) {
}
