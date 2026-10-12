package com.sosrescuenow.presentation.cuoc;

import jakarta.validation.constraints.Size;

public record HuyCuocRequest(
        @Size(max = 255, message = "Lý do tối đa 255 ký tự")
        String lyDo) {
}
