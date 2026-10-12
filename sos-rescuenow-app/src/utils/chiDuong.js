import { Alert, Linking } from 'react-native';

async function mo(url) {
  try {
    await Linking.openURL(url);
  } catch (e) {
    Alert.alert('Không mở được Google Maps', 'Hãy kiểm tra điện thoại có trình duyệt hoặc ứng dụng bản đồ.');
  }
}

/** Mo Google Maps voi duong di toi diem den (cho tho di toi khach). */
export function chiDuongToi({ latitude, longitude }) {
  return mo(`https://www.google.com/maps/dir/?api=1&destination=${latitude},${longitude}&travelmode=driving`);
}

/** Mo Google Maps tai mot diem (cho khach xem vi tri tho). */
export function xemTrenGoogleMaps({ latitude, longitude }) {
  return mo(`https://www.google.com/maps/search/?api=1&query=${latitude},${longitude}`);
}
