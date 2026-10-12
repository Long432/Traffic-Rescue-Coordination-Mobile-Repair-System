package com.sosrescuenow.domain.model;

import com.sosrescuenow.domain.exception.DuLieuKhongHopLeException;

/** Toa do dia ly (do thap phan). Tu kiem tra hop le khi tao. */
public record ViTri(double latitude, double longitude) {

    private static final double BAN_KINH_TRAI_DAT_KM = 6371.0088;

    public ViTri {
        if (Double.isNaN(latitude) || latitude < -90 || latitude > 90) {
            throw new DuLieuKhongHopLeException("Vĩ độ phải nằm trong khoảng từ -90 đến 90");
        }
        if (Double.isNaN(longitude) || longitude < -180 || longitude > 180) {
            throw new DuLieuKhongHopLeException("Kinh độ phải nằm trong khoảng từ -180 đến 180");
        }
    }

    /** Khoang cach (km) tren mat cau theo cong thuc Haversine. */
    public double khoangCachKm(ViTri khac) {
        double dLat = Math.toRadians(khac.latitude - latitude);
        double dLng = Math.toRadians(khac.longitude - longitude);
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(khac.latitude))
                * Math.pow(Math.sin(dLng / 2), 2);
        return 2 * BAN_KINH_TRAI_DAT_KM * Math.asin(Math.min(1.0, Math.sqrt(a)));
    }
}
