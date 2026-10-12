import React, { useEffect, useState } from 'react';
import { ActivityIndicator, Alert, ScrollView, StyleSheet, Text, View } from 'react-native';
import * as api from '../../api';
import BanDo from '../../components/BanDo';
import ChonSao from '../../components/ChonSao';
import ManHinhTai from '../../components/ManHinhTai';
import Nut from '../../components/Nut';
import The from '../../components/The';
import TrangThaiChip from '../../components/TrangThaiChip';
import TruongNhap from '../../components/TruongNhap';
import { banKinh, mau } from '../../theme';
import { xemTrenGoogleMaps } from '../../utils/chiDuong';
import { khoangCachKm } from '../../utils/khoangCach';
import { daKetThuc, formatKhoangCach, formatTien, khachCoTheHuy } from '../../utils/nhan';

const CHU_KY_HOI_MS = 3000;

const LOI_NHAN_KET_THUC = {
  HOAN_THANH: 'Yêu cầu đã hoàn thành. Cảm ơn bạn đã sử dụng dịch vụ!',
  HUY: 'Yêu cầu này đã được hủy.',
  KHONG_CO_THO: 'Rất tiếc, hiện chưa có thợ nào nhận. Bạn có thể gửi lại yêu cầu.',
};

export default function TheoDoiCuocScreen({ route, navigation }) {
  const { cuocId } = route.params;
  const [cuoc, setCuoc] = useState(null);
  const [tho, setTho] = useState(null);
  const [loi, setLoi] = useState(null);
  const [dangHuy, setDangHuy] = useState(false);
  const [danhGia, setDanhGia] = useState(undefined); // undefined: dang tai, null: chua danh gia, doi tuong: da danh gia
  const [soSao, setSoSao] = useState(0);
  const [nhanXet, setNhanXet] = useState('');
  const [dangGuiDanhGia, setDangGuiDanhGia] = useState(false);
  const hoanThanh = cuoc !== null && cuoc.trangThai === 'HOAN_THANH';

  // Hoi server moi 3 giay cho den khi cuoc ket thuc (WebSocket se thay the o module sau).
  useEffect(() => {
    let huy = false;
    let hen = null;

    const mot = async () => {
      try {
        const c = await api.xemCuoc(cuocId);
        if (huy) {
          return 'dung';
        }
        setCuoc(c);
        setLoi(null);
        if (daKetThuc(c.trangThai)) {
          setTho(null);
          return 'xong';
        }
        const t = c.thoId ? await api.viTriThoCuaCuoc(cuocId) : null;
        if (!huy) {
          setTho(t);
        }
      } catch (e) {
        if (!huy) {
          setLoi(e.message);
        }
      }
      return 'tiep';
    };

    const vong = async () => {
      const kq = await mot();
      if (!huy && kq === 'tiep') {
        hen = setTimeout(vong, CHU_KY_HOI_MS);
      }
    };
    vong();
    return () => {
      huy = true;
      clearTimeout(hen);
    };
  }, [cuocId]);

  // Khi cuoc hoan thanh: kiem tra khach da danh gia chua.
  useEffect(() => {
    if (!hoanThanh) {
      return undefined;
    }
    let huy = false;
    api
      .xemDanhGia(cuocId)
      .then((d) => {
        if (!huy) {
          setDanhGia(d);
        }
      })
      .catch(() => {
        if (!huy) {
          setDanhGia(null);
        }
      });
    return () => {
      huy = true;
    };
  }, [hoanThanh, cuocId]);

  const guiDanhGia = async () => {
    if (soSao < 1) {
      Alert.alert('Chưa chọn số sao', 'Hãy chạm vào số sao bạn muốn tặng thợ.');
      return;
    }
    setDangGuiDanhGia(true);
    try {
      setDanhGia(await api.danhGiaCuoc(cuocId, soSao, nhanXet.trim() || null));
    } catch (e) {
      Alert.alert('Không gửi được đánh giá', e.message);
    } finally {
      setDangGuiDanhGia(false);
    }
  };

  const huyYeuCau = () => {
    Alert.alert('Hủy yêu cầu?', 'Bạn chắc chắn muốn hủy yêu cầu cứu hộ này?', [
      { text: 'Không', style: 'cancel' },
      {
        text: 'Hủy yêu cầu',
        style: 'destructive',
        onPress: async () => {
          setDangHuy(true);
          try {
            setCuoc(await api.huyCuoc(cuocId, 'Khách hủy từ ứng dụng'));
          } catch (e) {
            Alert.alert('Không hủy được', e.message);
          } finally {
            setDangHuy(false);
          }
        },
      },
    ]);
  };

  if (!cuoc) {
    return loi ? (
      <View style={styles.giua}>
        <Text style={styles.loi}>{loi}</Text>
        <Nut nhan="Quay lại" kieu="phu" onPress={() => navigation.goBack()} />
      </View>
    ) : (
      <ManHinhTai noiDung="Đang tải yêu cầu..." />
    );
  }

  const ketThuc = daKetThuc(cuoc.trangThai);
  const coViTriTho = tho && typeof tho.latitude === 'number' && typeof tho.longitude === 'number';
  const diem = [{ id: 'khach', latitude: cuoc.latitude, longitude: cuoc.longitude, tieuDe: 'Vị trí của bạn', mau: mau.chinh }];
  if (coViTriTho) {
    diem.push({ id: 'tho', latitude: tho.latitude, longitude: tho.longitude, tieuDe: tho.hoTen, mau: mau.xanh });
  }
  const khoangCachTho = coViTriTho
    ? khoangCachKm(
        { latitude: tho.latitude, longitude: tho.longitude },
        { latitude: cuoc.latitude, longitude: cuoc.longitude },
      )
    : null;

  return (
    <ScrollView contentContainerStyle={styles.noiDung}>
      <BanDo style={styles.banDo} diem={diem} />

      <The style={styles.theTrenBanDo}>
        <TrangThaiChip trangThai={cuoc.trangThai} />

        {cuoc.trangThai === 'DANG_TIM' ? (
          <View style={styles.hang}>
            <ActivityIndicator color={mau.cam} />
            <Text style={styles.chuHang}>Đang tìm thợ gần bạn (tối đa 2 phút)...</Text>
          </View>
        ) : null}

        {tho ? (
          <View style={styles.khoiTho}>
            <Text style={styles.tenTho}>Thợ: {tho.hoTen}</Text>
            {khoangCachTho !== null ? (
              <Text style={styles.phu}>Cách bạn khoảng {formatKhoangCach(khoangCachTho)}</Text>
            ) : (
              <Text style={styles.phu}>Đang chờ thợ gửi vị trí...</Text>
            )}
          </View>
        ) : null}

        <View style={styles.khoiTien}>
          <Text style={styles.phu}>Giá ước tính</Text>
          <Text style={styles.tien}>{formatTien(cuoc.giaUocTinh)}</Text>
          {cuoc.tongTien !== null && cuoc.tongTien !== undefined ? (
            <Text style={styles.tongTien}>Tổng thanh toán: {formatTien(cuoc.tongTien)}</Text>
          ) : null}
        </View>

        {cuoc.diaChi ? <Text style={styles.phu}>Địa chỉ: {cuoc.diaChi}</Text> : null}
        {cuoc.trangThai === 'HUY' && cuoc.lyDoHuy ? <Text style={styles.phu}>Lý do: {cuoc.lyDoHuy}</Text> : null}
        {loi ? <Text style={styles.loi}>Mất kết nối tạm thời: {loi}</Text> : null}
      </The>

      {!ketThuc && coViTriTho ? (
        <Nut
          nhan="Xem vị trí thợ trong Google Maps"
          kieu="phu"
          onPress={() => xemTrenGoogleMaps({ latitude: tho.latitude, longitude: tho.longitude })}
          style={styles.nutXemBanDo}
        />
      ) : null}

      {cuoc.trangThai === 'HOAN_THANH' ? (
        <The style={styles.theDanhGia}>
          {danhGia === undefined ? (
            <ActivityIndicator color={mau.cam} />
          ) : danhGia ? (
            <View>
              <Text style={styles.tieuDeDanhGia}>Cảm ơn bạn đã đánh giá!</Text>
              <ChonSao giaTri={danhGia.soSao} />
              {danhGia.nhanXet ? <Text style={styles.nhanXet}>{danhGia.nhanXet}</Text> : null}
            </View>
          ) : (
            <View>
              <Text style={styles.tieuDeDanhGia}>Bạn thấy thợ phục vụ thế nào?</Text>
              <ChonSao giaTri={soSao} onChon={setSoSao} />
              <TruongNhap
                nhan="Nhận xét (không bắt buộc)"
                value={nhanXet}
                onChangeText={setNhanXet}
                maxLength={500}
                multiline
              />
              <Nut nhan="Gửi đánh giá" kieu="xanh" onPress={guiDanhGia} dangTai={dangGuiDanhGia} />
            </View>
          )}
        </The>
      ) : null}

      {ketThuc ? (
        <View>
          <Text style={styles.ketThuc}>{LOI_NHAN_KET_THUC[cuoc.trangThai]}</Text>
          <Nut nhan="Về trang chính" onPress={() => navigation.popToTop()} />
        </View>
      ) : null}

      {!ketThuc && khachCoTheHuy(cuoc.trangThai) ? (
        <Nut nhan="Hủy yêu cầu" kieu="phu" onPress={huyYeuCau} dangTai={dangHuy} />
      ) : null}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  noiDung: { paddingBottom: 32 },
  giua: { flex: 1, alignItems: 'center', justifyContent: 'center', padding: 24, gap: 12 },
  banDo: { height: 280, width: '100%' },
  theTrenBanDo: { margin: 16, marginTop: -24, borderRadius: banKinh },
  hang: { flexDirection: 'row', alignItems: 'center', marginTop: 12, gap: 10 },
  chuHang: { color: mau.chu, flex: 1 },
  khoiTho: { marginTop: 12 },
  tenTho: { fontSize: 17, fontWeight: '700', color: mau.chu },
  khoiTien: { marginTop: 12, marginBottom: 6 },
  tien: { fontSize: 22, fontWeight: '800', color: mau.chu },
  tongTien: { fontSize: 16, fontWeight: '700', color: mau.xanhLa, marginTop: 4 },
  phu: { color: mau.chuPhu, marginTop: 2 },
  loi: { color: mau.loi, marginTop: 8 },
  theDanhGia: { marginHorizontal: 16 },
  tieuDeDanhGia: { fontSize: 17, fontWeight: '700', color: mau.chu, textAlign: 'center' },
  nhanXet: { textAlign: 'center', color: mau.chuPhu, fontStyle: 'italic' },
  nutXemBanDo: { marginHorizontal: 16, marginBottom: 12 },
  ketThuc: { marginHorizontal: 16, marginBottom: 12, fontSize: 16, color: mau.chu, textAlign: 'center' },
});
