package com.sosrescuenow.domain.model;

/** Thong tin tom tat cua tho de admin xem va duyet. */
public record ThoTomTat(Integer id, String hoTen, String soDienThoai, boolean daDuyet,
                        boolean online, boolean dangBan, double diemTrungBinh) {
}
