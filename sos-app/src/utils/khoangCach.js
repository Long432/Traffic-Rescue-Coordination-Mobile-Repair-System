const BAN_KINH_TRAI_DAT_KM = 6371.0088;

/** Khoang cach (km) giua hai toa do theo cong thuc Haversine (giong server). */
export function khoangCachKm(a, b) {
  const rad = (d) => (d * Math.PI) / 180;
  const dLat = rad(b.latitude - a.latitude);
  const dLng = rad(b.longitude - a.longitude);
  const x =
    Math.sin(dLat / 2) ** 2 +
    Math.cos(rad(a.latitude)) * Math.cos(rad(b.latitude)) * Math.sin(dLng / 2) ** 2;
  return 2 * BAN_KINH_TRAI_DAT_KM * Math.asin(Math.min(1, Math.sqrt(x)));
}
