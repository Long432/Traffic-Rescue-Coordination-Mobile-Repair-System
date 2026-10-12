package com.sosrescuenow.infrastructure.persistence.repository;

import java.math.BigDecimal;

/** Mot dong danh sach tho cho admin (constructor expression cua HQL). */
public record ThoTomTatRow(Integer id, String hoTen, String soDienThoai, Boolean daDuyet,
                           Boolean online, Boolean dangBan, BigDecimal diemTrungBinh) {
}
