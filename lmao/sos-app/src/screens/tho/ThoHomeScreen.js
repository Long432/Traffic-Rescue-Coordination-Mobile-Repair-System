import React, { useCallback, useEffect, useState } from 'react';
import { Alert, ScrollView, StyleSheet, Switch, Text, View } from 'react-native';
import { useFocusEffect, useIsFocused } from '@react-navigation/native';
import * as api from '../../api';
import ManHinhTai from '../../components/ManHinhTai';
import Nut from '../../components/Nut';
import The from '../../components/The';
import { useAuth } from '../../auth/AuthContext';
import { useTho } from '../../tho/ThoContext';
import { mau } from '../../theme';
import { formatKhoangCach, formatTien } from '../../utils/nhan';

const CHU_KY_HOI_MS = 4000;

export default function ThoHomeScreen({ navigation }) {
  const { phien } = useAuth();
  const { hoSo, viTri, taiHoSo, datOnline } = useTho();
  const dangMo = useIsFocused();
  const [danhSach, setDanhSach] = useState([]);
  const [tenLoai, setTenLoai] = useState({});
  const [dangDoi, setDangDoi] = useState(false);
  const [dangNhan, setDangNhan] = useState(null);
  const [loi, setLoi] = useState(null);

  // Mo/quay lai man hinh: tai ho so, ten loai su co; neu dang co cuoc thi chuyen thang vao cuoc do.
  useFocusEffect(
    useCallback(() => {
      let huy = false;
      (async () => {
        try {
          const [, cuocDangLam, dsLoai] = await Promise.all([taiHoSo(), api.thoCuocHienTai(), api.loaiSuCo()]);
          if (huy) {
            return;
          }
          const bang = {};
          dsLoai.forEach((l) => {
            bang[l.id] = l.ten;
          });
          setTenLoai(bang);
          setLoi(null);
          if (cuocDangLam) {
            navigation.navigate('ThoCuoc', { cuocId: cuocDangLam.id });
          }
        } catch (e) {
          if (!huy) {
            setLoi(e.message);
          }
        }
      })();
      return () => {
        huy = true;
      };
    }, [navigation, taiHoSo]),
  );

  const sanSang = Boolean(hoSo && hoSo.online && !hoSo.dangBan && hoSo.daDuyet);

  // Khi san sang va dang o man hinh nay: hoi cuoc quanh day moi 4 giay.
  useEffect(() => {
    if (!sanSang || !dangMo) {
      setDanhSach([]);
      return undefined;
    }
    let huy = false;
    let hen = null;
    const vong = async () => {
      try {
        const ds = await api.thoCuocQuanhToi();
        if (!huy) {
          setDanhSach(ds);
          setLoi(null);
        }
      } catch (e) {
        if (!huy) {
          setLoi(e.message);
        }
      }
      if (!huy) {
        hen = setTimeout(vong, CHU_KY_HOI_MS);
      }
    };
    vong();
    return () => {
      huy = true;
      clearTimeout(hen);
    };
  }, [sanSang, dangMo]);

  const doiTrangThai = async (giaTri) => {
    setDangDoi(true);
    try {
      await datOnline(giaTri);
    } catch (e) {
      Alert.alert('Không đổi được trạng thái', e.message);
    } finally {
      setDangDoi(false);
    }
  };

  const nhan = async (cuocId) => {
    setDangNhan(cuocId);
    try {
      await api.thoNhanCuoc(cuocId);
      await taiHoSo();
      navigation.navigate('ThoCuoc', { cuocId });
    } catch (e) {
      Alert.alert('Không nhận được cuốc', e.message);
    } finally {
      setDangNhan(null);
    }
  };

  if (!hoSo && !loi) {
    return <ManHinhTai />;
  }

  return (
    <ScrollView contentContainerStyle={styles.noiDung}>
      <Text style={styles.chao}>Xin chào, {phien.hoTen}</Text>

      {loi ? (
        <The>
          <Text style={styles.loi}>{loi}</Text>
        </The>
      ) : null}

      {hoSo && !hoSo.daDuyet ? (
        <The style={styles.theCanhBao}>
          <Text style={styles.tieuDeThe}>Hồ sơ thợ chưa được duyệt</Text>
          <Text style={styles.phu}>
            Mã thợ của bạn là #{phien.nguoiDungId}. Hãy gửi mã này cho quản trị viên để được duyệt, sau đó mở lại màn hình này.
          </Text>
        </The>
      ) : null}

      {hoSo ? (
        <The>
          <View style={styles.hangCongTac}>
            <View style={styles.nhanCongTac}>
              <Text style={styles.tieuDeThe}>Sẵn sàng nhận cuốc</Text>
              <Text style={styles.phu}>
                {hoSo.online ? 'Đang online, vị trí được gửi 5 giây một lần' : 'Đang offline'}
              </Text>
            </View>
            <Switch
              value={hoSo.online}
              onValueChange={doiTrangThai}
              disabled={dangDoi || !hoSo.daDuyet}
              trackColor={{ true: mau.xanhLa, false: mau.vien }}
            />
          </View>
          {hoSo.online ? (
            <Text style={styles.viTri}>
              {viTri
                ? `Vị trí của bạn: ${viTri.latitude.toFixed(5)}, ${viTri.longitude.toFixed(5)}`
                : 'Đang lấy vị trí... (nếu dùng máy ảo, hãy đặt vị trí cho máy ảo)'}
            </Text>
          ) : null}
        </The>
      ) : null}

      {sanSang ? (
        <View>
          <Text style={styles.nhomTieuDe}>Cuốc quanh bạn ({danhSach.length})</Text>
          {danhSach.length === 0 ? (
            <Text style={styles.trong}>
              Chưa có cuốc nào dành cho bạn. Hãy giữ ứng dụng mở, danh sách tự cập nhật.
            </Text>
          ) : null}
          {danhSach.map((c) => (
            <The key={c.cuoc.id}>
              <View style={styles.hangCuoc}>
                <Text style={styles.tenLoai}>{tenLoai[c.cuoc.loaiSuCoId] || `Loại #${c.cuoc.loaiSuCoId}`}</Text>
                <Text style={styles.gia}>{formatTien(c.cuoc.giaUocTinh)}</Text>
              </View>
              <Text style={styles.phu}>Cách bạn {formatKhoangCach(c.khoangCachKm)}</Text>
              {c.cuoc.diaChi ? <Text style={styles.phu}>Địa chỉ: {c.cuoc.diaChi}</Text> : null}
              {c.cuoc.ghiChu ? <Text style={styles.phu}>Ghi chú: {c.cuoc.ghiChu}</Text> : null}
              <Nut
                nhan="NHẬN CUỐC"
                kieu="xanh"
                onPress={() => nhan(c.cuoc.id)}
                dangTai={dangNhan === c.cuoc.id}
                voHieu={dangNhan !== null && dangNhan !== c.cuoc.id}
                style={styles.nutCach}
              />
            </The>
          ))}
        </View>
      ) : null}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  noiDung: { padding: 16, paddingBottom: 40 },
  chao: { fontSize: 20, fontWeight: '700', color: mau.chu, marginBottom: 12 },
  theCanhBao: { borderColor: mau.cam },
  tieuDeThe: { fontWeight: '700', fontSize: 16, color: mau.chu },
  phu: { color: mau.chuPhu, marginTop: 4 },
  loi: { color: mau.loi },
  hangCongTac: { flexDirection: 'row', alignItems: 'center' },
  nhanCongTac: { flex: 1, paddingRight: 12 },
  viTri: { marginTop: 10, color: mau.chuPhu, fontSize: 13 },
  nhomTieuDe: { fontSize: 16, fontWeight: '700', color: mau.chu, marginTop: 8, marginBottom: 10 },
  trong: { color: mau.chuPhu, textAlign: 'center', marginVertical: 24 },
  hangCuoc: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  tenLoai: { fontSize: 17, fontWeight: '700', color: mau.chu, flex: 1 },
  gia: { fontSize: 17, fontWeight: '800', color: mau.xanhLa },
  nutCach: { marginTop: 12 },
});
