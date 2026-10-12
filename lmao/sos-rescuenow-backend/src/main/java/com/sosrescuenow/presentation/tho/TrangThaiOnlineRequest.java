package com.sosrescuenow.presentation.tho;

import jakarta.validation.constraints.NotNull;

public record TrangThaiOnlineRequest(
        @NotNull(message = "Trạng thái online không được để trống") Boolean online) {
}
