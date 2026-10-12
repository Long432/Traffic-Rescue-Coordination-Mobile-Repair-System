package com.sosrescuenow.application.port;

import com.sosrescuenow.domain.model.LoaiSuCo;

import java.util.List;
import java.util.Optional;

public interface LoaiSuCoRepository {

    Optional<LoaiSuCo> timTheoId(Integer id);

    List<LoaiSuCo> timTatCa();
}
