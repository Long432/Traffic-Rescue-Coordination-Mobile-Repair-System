package com.sosrescuenow.domain.model;

import java.time.LocalDateTime;

/** Vi tri moi nhat ma tho da gui len. */
public record ViTriTho(Integer thoId, ViTri viTri, LocalDateTime capNhatLuc) {
}
