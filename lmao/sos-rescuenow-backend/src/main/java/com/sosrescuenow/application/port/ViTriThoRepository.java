package com.sosrescuenow.application.port;

import com.sosrescuenow.domain.model.ViTri;
import com.sosrescuenow.domain.model.ViTriTho;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ViTriThoRepository {

    /** Ghi de vi tri moi nhat cua tho (mot dong moi tho). */
    void luu(Integer thoId, ViTri viTri, LocalDateTime luc);

    Optional<ViTriTho> timTheoThoId(Integer thoId);
}
