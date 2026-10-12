import React, { useCallback, useEffect, useState } from 'react';
import { Alert, BackHandler, ScrollView, StyleSheet, Text, View } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import * as api from '../../api';
import BanDo from '../../components/BanDo';
import HopThoaiLyDo from '../../components/HopThoaiLyDo';
import ManHinhTai from '../../components/ManHinhTai';
import Nut from '../../components/Nut';
import The from '../../components/The';
import TrangThaiChip from '../../components/TrangThaiChip';
import { useTho } from '../../tho/ThoContext';
import { banKinh, mau } from '../../theme';
import { khoangCachKm } from '../../utils/khoangCach';
import { BUOC_TIEP_CUA_THO, daKetThuc, formatKhoangCach, formatTien, thoCoTheTra } from '../../utils/nhan';

const CHU_KY_HOI_MS = 4000;
const BAN_KINH_CHECK_IN_MET = 100;

export default function ThoCuocScreen({ route, navigation }) {
  const { cuocId } = route.params;
  const { viTri, taiHoSo } = useTho();
  const [cuoc, setCuoc] = useState(null);
  const [tenLoai, setTenLoai] = useState('');
  const [loi, setLoi] = useState(null);
  const [dangDoi, setDangDoi] = useState(false);
  const [hienTraCuoc, setHienTraCuoc] = useState(false);

  const ketThuc = cuoc ? daKetThuc(cuoc.trangThai) : false;

  // Ten loai su co (tai mot lan).
  useEffect(() => {
    let huy = false;
    api
      .loaiSuCo()
      .then((ds) => {
        const l = ds.find((x) => cuoc && x.id === cuoc.loaiSuCoId);
        if (!huy && l) {
          setTenLoai(l.ten);
        }
      })
      .catch(() => {});
    return () => {
      huy = true;
    };
    // chay lai khi da biet loai su co cua cuoc
  }, [cuoc && cuoc.loaiSuCoId]); // eslint-disable-line react-hooks/exhaustive-deps

  // Hoi trang thai cuoc moi 4 giay den khi ket thuc (khach co the huy bat cu luc nao).
  useEffect(() => {
    let huy = false;
    let hen = null;
    const vong = async () => {
      let tiep = true;
      try {
        const c = await api.xemCuoc(cuocId);
        if (huy) {
          return;
        }
        setCuoc(c);
        setLoi(null);
        if (daKetThuc(c.trangThai)) {
          tiep = false;
          taiHoSo().catch(() => {});
        }
      } catch (e) {
        if (!huy) {
          setLoi(e.message);
        }
      }
      if (!huy && tiep) {
        hen = setTimeout(vong, CHU_KY_HOI_MS);
      }
    };
    vong();
    return () => {
      huy = true;
      clearTimeout(hen);
    };
  }, [cuocId, taiHoSo]);

  // Khong cho thoat bang nut Back cua Android khi cuoc chua ket thuc.
  useFocusEffect(
    useCallback(() => {
      const sub = BackHandler.addEventListener('hardwareBackPress', () => !ketThuc);
      return () => sub.remove();
    }, [ketThuc]),
  );

  const chuyen = async (den) => {
    setDangDoi(true);
    try {
      setCuoc(await api.thoChuyenTrangThai(cuocId, den));
    } catch (e) {
      Alert.alert('Chưa chuyển được bước', e.message);
    } finally {
      setDangDoi(false);
    }
  };

  const traCuoc = async (lyDo) => {
    setHienTraCuoc(false);
    setDangDoi(true);
    try {
      await api.thoTraCuoc(cuocId, lyDo);
      await taiHoSo();
      navigation.navigate('ThoHome');
    } catch (e) {
      Alert.alert('Không trả được cuốc', e.message);
    } finally {
      setDangDoi(false);
    }
  };

  if (!cuoc) {
    return loi ? (
      <View style={styles.giua}>
        <Text style={styles.loi}>{loi}</Text>
        <Nut nhan="Về trang chính" kieu="phu" onPress={() => navigation.navigate('ThoHome')} />
      </View>
    ) : (
      <ManHinhTai noiDung="Đang tải cuốc..." />
    );
  }

  const buoc = BUOC_TIEP_CUA_THO[cuoc.trangThai];
  const diem = [{ id: 'khach', latitude: cuoc.latitude, longitude: cuoc.longitude, tieuDe: 'Khách hàng', mau: mau.chinh }];
  if (viTri) {
    diem.push({ id: 'tho', ...viTri, tieuDe: 'Bạn', mau: mau.xanh });
  }
  const met = viTri
    ? khoangCachKm(viTri, { latitude: cuoc.latitude, longitude: cuoc.longitude }) * 1000
    : null;

  return (
    <ScrollView contentContainerStyle={styles.noiDung}>
      <BanDo style={styles.banDo} diem={diem} />

      <The style={styles.theTrenBanDo}>
        <TrangThaiChip trangThai={cuoc.trangThai} />
        <Text style={styles.tenLoai}>{tenLoai || `Cuốc #${cuoc.id}`}</Text>
        {cuoc.diaChi ? <Text style={styles.phu}>Địa chỉ: {cuoc.diaChi}</Text> : null}
        {cuoc.ghiChu ? <Text style={styles.phu}>Ghi chú: {cuoc.ghiChu}</Text> : null}
        <Text style={styles.tien}>
          {cuoc.tongTien !== null && cuoc.tongTien !== undefined
            ? `Cần thu: ${formatTien(cuoc.tongTien)}`
            : `Giá ước tính: ${formatTien(cuoc.giaUocTinh)}`}
        </Text>
        {met !== null && !ketThuc ? (
          <Text style={[styles.khoangCach, met <= BAN_KINH_CHECK_IN_MET && styles.khoangCachDu]}>
            Cách khách {formatKhoangCach(met / 1000)}
            {cuoc.trangThai === 'DANG_DEN' ? ` (cần trong vòng ${BAN_KINH_CHECK_IN_MET} m để bấm "Đã đến nơi")` : ''}
          </Text>
        ) : null}
        {loi ? <Text style={styles.loi}>Mất kết nối tạm thời: {loi}</Text> : null}
      </The>

      {ketThuc ? (
        <View style={styles.khoiKetThuc}>
          <Text style={styles.ketThuc}>
            {cuoc.trangThai === 'HOAN_THANH'
              ? `Hoàn thành! Số tiền thu: ${formatTien(cuoc.tongTien)}`
              : 'Khách đã hủy yêu cầu này.'}
          </Text>
          <Nut nhan="Về trang chính" onPress={() => navigation.navigate('ThoHome')} />
        </View>
      ) : (
        <View style={styles.khoiNut}>
          {buoc ? <Nut nhan={buoc.nhan} kieu="xanh" onPress={() => chuyen(buoc.den)} dangTai={dangDoi} /> : null}
          {thoCoTheTra(cuoc.trangThai) ? (
            <Nut nhan="Trả cuốc" kieu="phu" onPress={() => setHienTraCuoc(true)} voHieu={dangDoi} style={styles.nutTra} />
          ) : null}
        </View>
      )}

      <HopThoaiLyDo
        hienThi={hienTraCuoc}
        tieuDe="Vì sao bạn trả cuốc này?"
        goiY="Ví dụ: xe tôi bị hỏng, khách ở quá xa..."
        onXacNhan={traCuoc}
        onDong={() => setHienTraCuoc(false)}
      />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  noiDung: { paddingBottom: 32 },
  giua: { flex: 1, alignItems: 'center', justifyContent: 'center', padding: 24, gap: 12 },
  banDo: { height: 280, width: '100%' },
  theTrenBanDo: { margin: 16, marginTop: -24, borderRadius: banKinh },
  tenLoai: { fontSize: 20, fontWeight: '800', color: mau.chu, marginTop: 10 },
  phu: { color: mau.chuPhu, marginTop: 4 },
  tien: { fontSize: 17, fontWeight: '700', color: mau.xanhLa, marginTop: 10 },
  khoangCach: { marginTop: 8, color: mau.cam, fontWeight: '600' },
  khoangCachDu: { color: mau.xanhLa },
  loi: { color: mau.loi, marginTop: 8 },
  khoiNut: { paddingHorizontal: 16 },
  nutTra: { marginTop: 12 },
  khoiKetThuc: { paddingHorizontal: 16 },
  ketThuc: { fontSize: 17, fontWeight: '700', color: mau.chu, textAlign: 'center', marginBottom: 14 },
});
