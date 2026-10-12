package com.sosrescuenow.application.port;

import com.sosrescuenow.domain.model.VaiTro;

import java.time.Duration;

public interface TokenIssuer {

    String phatHanh(Integer nguoiDungId, VaiTro vaiTro);

    /** Thoi gian song cua mot token. */
    Duration thoiHan();
}
