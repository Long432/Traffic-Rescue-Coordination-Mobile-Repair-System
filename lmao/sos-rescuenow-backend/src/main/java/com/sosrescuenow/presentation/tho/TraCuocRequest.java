package com.sosrescuenow.presentation.tho;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TraCuocRequest(
        @NotBlank(message = "Cần ghi lý do trả cuốc")
        @Size(max = 255, message = "Lý do tối đa 255 ký tự")
        String lyDo) {
}
