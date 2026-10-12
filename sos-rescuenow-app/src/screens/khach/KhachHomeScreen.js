import React, { useCallback, useEffect, useState } from 'react';
import { Alert, ScrollView, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import * as api from '../../api';
import BanDo from '../../components/BanDo';
import ManHinhTai from '../../components/ManHinhTai';
import Nut from '../../components/Nut';
import The from '../../components/The';
import TrangThaiChip from '../../components/TrangThaiChip';
import TruongNhap from '../../components/TruongNhap';
import { useAuth } from '../../auth/AuthContext';
import { banKinh, mau } from '../../theme';
import { daKetThuc, formatTien } from '../../utils/nhan';
import { layDiaChi, layViTriHienTai } from '../../utils/viTri';

export default function KhachHomeScreen({ navigation }) {
  const { phien } = useAuth();
  const [loai, setLoai] = useState([]);
  const [chon, setChon] = useState(null);
  const [viTri, setViTri] = useState(null);
  const [loiViTri, setLoiViTri] = useState(null);
  const [diaChi, setDiaChi] = useState('');
  const [ghiChu, setGhiChu] = useState('');
  const [cuocDangCo, setCuocDangCo] = useState(null);
  const [dangTai, setDangTai] = useState(true);
  const [loiTai, setLoiTai] = useState(null);
  const [dangLayViTri, setDangLayViTri] = useState(false);
  const [dangGui, setDangGui] = useState(false);

  const tai = useCallback(async () => {
    try {
      const [dsLoai, dsCuoc] = await Promise.all([api.loaiSuCo(), api.cuocCuaToi()]);
      setLoai(dsLoai);
      setChon((cu) => (cu !== null ? cu : dsLoai.length > 0 ? dsLoai[0].id : null));
      setCuocDangCo(dsCuoc.find((c) => !daKetThuc(c.trangThai)) || null);
      setLoiTai(null);
    } catch (e) {
      setLoiTai(e.message);
    } finally {
      setDangTai(false);
    }
  }, []);

  // Moi lan quay lai man hinh nay: tai lai (de thay yeu cau dang xu ly).
  useFocusEffect(
    useCallback(() => {
      tai();
    }, [tai]),
  );

  const layViTri = useCallback(async () => {
    setDangLayViTri(true);
    setLoiViTri(null);
    try {
      const v = await layViTriHienTai();
      setViTri(v);
      const dc = await layDiaChi(v);
      if (dc) {
        setDiaChi(dc);
      }
    } catch (e) {
      setLoiViTri(e.message);
    } finally {
      setDangLayViTri(false);
    }
  }, []);

  // Tu dong lay vi tri mot lan khi mo man hinh.
  useEffect(() => {
    layViTri();
  }, [layViTri]);

  const gui = async () => {
    if (chon === null) {
      Alert.alert('Chưa chọn loại sự cố', 'Hãy chọn xe bạn gặp sự cố gì.');
      return;
    }
    if (!viTri) {
      Alert.alert('Chưa có vị trí', 'Hãy bấm "Lấy vị trí của tôi" để thợ biết đường đến.');
      return;
    }
    setDangGui(true);
    try {
      const kq = await api.guiSos({
        loaiSuCoId: chon,
        latitude: viTri.latitude,
        longitude: viTri.longitude,
        diaChi: diaChi.trim() || null,
        ghiChu: ghiChu.trim() || null,
      });
      setGhiChu('');
      navigation.navigate('TheoDoiCuoc', { cuocId: kq.cuoc.id });
    } catch (e) {
      Alert.alert('Không gửi được yêu cầu', e.message);
    } finally {
      setDangGui(false);
    }
  };

  if (dangTai) {
    return <ManHinhTai />;
  }

  return (
    <ScrollView contentContainerStyle={styles.noiDung} keyboardShouldPersistTaps="handled">
      <Text style={styles.chao}>Xin chào, {phien.hoTen}</Text>

      {loiTai ? (
        <The>
          <Text style={styles.loi}>{loiTai}</Text>
          <Nut nhan="Thử lại" kieu="phu" onPress={tai} style={styles.nutCach} />
        </The>
      ) : null}

      {cuocDangCo ? (
        <The style={styles.theDangCo}>
          <Text style={styles.tieuDeThe}>Bạn đang có một yêu cầu cứu hộ</Text>
          <TrangThaiChip trangThai={cuocDangCo.trangThai} />
          <Nut
            nhan="Xem yêu cầu đang xử lý"
            kieu="xanh"
            onPress={() => navigation.navigate('TheoDoiCuoc', { cuocId: cuocDangCo.id })}
            style={styles.nutCach}
          />
        </The>
      ) : null}

      <Text style={styles.nhomTieuDe}>1. Bạn gặp sự cố gì?</Text>
      {loai.map((l) => (
        <TouchableOpacity
          key={l.id}
          onPress={() => setChon(l.id)}
          activeOpacity={0.8}
          style={[styles.dong, chon === l.id && styles.dongChon]}
        >
          <View style={[styles.diem, chon === l.id && styles.diemChon]} />
          <Text style={styles.tenLoai}>{l.ten}</Text>
          <Text style={styles.gia}>từ {formatTien(l.giaCoBan)}</Text>
        </TouchableOpacity>
      ))}

      <Text style={styles.nhomTieuDe}>2. Bạn đang ở đâu?</Text>
      {viTri ? (
        <BanDo
          style={styles.banDo}
          diem={[{ id: 'toi', ...viTri, tieuDe: 'Vị trí của bạn', mau: mau.chinh }]}
        />
      ) : null}
      {loiViTri ? <Text style={styles.loi}>{loiViTri}</Text> : null}
      <Nut
        nhan={viTri ? 'Cập nhật vị trí của tôi' : 'Lấy vị trí của tôi'}
        kieu="phu"
        onPress={layViTri}
        dangTai={dangLayViTri}
        style={styles.nutCach}
      />
      <TruongNhap nhan="Địa chỉ (tự điền, có thể sửa)" value={diaChi} onChangeText={setDiaChi} maxLength={255} />
      <TruongNhap
        nhan="Ghi chú cho thợ (không bắt buộc)"
        value={ghiChu}
        onChangeText={setGhiChu}
        maxLength={500}
        multiline
      />

      <Nut nhan="GỬI SOS" onPress={gui} dangTai={dangGui} voHieu={Boolean(cuocDangCo)} style={styles.nutSos} />
      {cuocDangCo ? <Text style={styles.luuY}>Hãy hoàn tất hoặc hủy yêu cầu hiện tại trước khi gửi yêu cầu mới.</Text> : null}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  noiDung: { padding: 16, paddingBottom: 40 },
  chao: { fontSize: 20, fontWeight: '700', color: mau.chu, marginBottom: 12 },
  nhomTieuDe: { fontSize: 16, fontWeight: '700', color: mau.chu, marginTop: 8, marginBottom: 10 },
  theDangCo: { borderColor: mau.xanh },
  tieuDeThe: { fontWeight: '700', marginBottom: 8, color: mau.chu },
  nutCach: { marginTop: 10, marginBottom: 12 },
  dong: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: mau.vien,
    borderRadius: banKinh,
    padding: 14,
    marginBottom: 8,
  },
  dongChon: { borderColor: mau.chinh, backgroundColor: '#fdecea' },
  diem: { width: 18, height: 18, borderRadius: 9, borderWidth: 2, borderColor: mau.xam, marginRight: 12 },
  diemChon: { borderColor: mau.chinh, backgroundColor: mau.chinh },
  tenLoai: { flex: 1, fontSize: 16, color: mau.chu },
  gia: { color: mau.chuPhu, fontSize: 13 },
  banDo: { height: 180, borderRadius: banKinh, overflow: 'hidden', marginBottom: 4 },
  loi: { color: mau.loi, marginVertical: 6 },
  nutSos: { marginTop: 8, minHeight: 56 },
  luuY: { color: mau.cam, textAlign: 'center', marginTop: 8 },
});
