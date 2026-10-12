package com.sosrescuenow.presentation.tho;

import com.sosrescuenow.domain.model.TrangThaiCuoc;
import jakarta.validation.constraints.NotNull;

public record ChuyenTrangThaiRequest(
        @NotNull(message = "Trạng thái không được để trống") TrangThaiCuoc trangThai) {
}
