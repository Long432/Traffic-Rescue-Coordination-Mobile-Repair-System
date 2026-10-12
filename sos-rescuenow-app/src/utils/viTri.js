import * as Location from 'expo-location';

/** Xin quyen vi tri khi dang mo app. Tra ve true neu duoc cap. */
export async function xinQuyenViTri() {
  const { status } = await Location.requestForegroundPermissionsAsync();
  return status === 'granted';
}

/** Lay vi tri hien tai (xin quyen neu can). Nem loi tieng Viet de hien thi truc tiep. */
export async function layViTriHienTai() {
  if (!(await xinQuyenViTri())) {
    throw new Error('Bạn cần cấp quyền vị trí để dùng tính năng này.');
  }
  try {
    const vt = await Location.getCurrentPositionAsync({ accuracy: Location.Accuracy.Balanced });
    return { latitude: vt.coords.latitude, longitude: vt.coords.longitude };
  } catch (e) {
    throw new Error('Không lấy được vị trí. Hãy bật GPS (hoặc đặt vị trí cho máy ảo) rồi thử lại.');
  }
}

/** Doi toa do thanh dia chi chu. Loi mang/dich vu -> tra ve chuoi rong, khong lam hong luong chinh. */
export async function layDiaChi({ latitude, longitude }) {
  try {
    const [d] = await Location.reverseGeocodeAsync({ latitude, longitude });
    if (!d) {
      return '';
    }
    return [d.streetNumber, d.street, d.district || d.subregion, d.city].filter(Boolean).join(', ');
  } catch (e) {
    return '';
  }
}
