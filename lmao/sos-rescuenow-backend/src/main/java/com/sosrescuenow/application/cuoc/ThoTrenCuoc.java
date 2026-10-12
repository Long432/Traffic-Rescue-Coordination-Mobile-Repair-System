package com.sosrescuenow.application.cuoc;

import com.sosrescuenow.domain.model.ViTri;

import java.time.LocalDateTime;

/** Thong tin tho dang phu trach mot cuoc. {@code viTri} va {@code capNhatLuc} la null neu tho chua gui vi tri. */
public record ThoTrenCuoc(Integer thoId, String hoTen, ViTri viTri, LocalDateTime capNhatLuc) {
}
