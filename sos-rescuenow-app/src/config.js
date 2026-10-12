// ============================================================
// DIA CHI MAY CHU BACKEND  (SUA DONG DUOI THEO MAY CUA BAN)
//
//  - Dung dien thoai that (Expo Go):  http://<IP-may-tinh>:8080
//      Tim IP: mo PowerShell, go  ipconfig  -> lay "IPv4 Address" cua Wi-Fi, vi du 192.168.1.23
//      Dien thoai va may tinh phai cung mot mang Wi-Fi.
//  - May ao Android (Android Studio):  http://10.0.2.2:8080
//  - May ao iOS (chi macOS):           http://localhost:8080
// ============================================================
export const API_URL = 'http://192.168.1.23:8080';

// ============================================================
// KIEU BAN DO
//  'osm'    : ban do OpenStreetMap hien trong WebView. Khong can khoa API, chay duoc trong Expo Go.
//             (KHUYEN DUNG khi thu bang Expo Go SDK 57 tren Android, vi ban do Google trong Expo Go
//              dang bi loi hien nen den - loi da duoc bao cao tren GitHub cua Expo.)
//  'native' : ban do goc (Google Maps tren Android, Apple Maps tren iOS) qua react-native-maps.
//             Dung khi dong goi ung dung that (can khoa Google Maps API) hoac khi Expo Go da sua loi.
// ============================================================
export const KIEU_BAN_DO = 'osm';
