package com.sosrescuenow.infrastructure.persistence.repository;

import java.math.BigDecimal;

/** Mot dong ket qua truy van ung vien tho (dung cho constructor expression cua HQL). */
public record UngVienRow(Integer thoId, BigDecimal latitude, BigDecimal longitude,
                         BigDecimal diemTrungBinh, BigDecimal tiLeNhanCuoc) {
}
