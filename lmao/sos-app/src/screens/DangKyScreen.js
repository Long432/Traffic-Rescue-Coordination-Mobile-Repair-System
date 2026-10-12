import React, { useState } from 'react';
import { Alert, KeyboardAvoidingView, Platform, ScrollView, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import Nut from '../components/Nut';
import TruongNhap from '../components/TruongNhap';
import { useAuth } from '../auth/AuthContext';
import { banKinh, mau } from '../theme';
import { SDT_HOP_LE } from '../utils/nhan';

const VAI_TRO = [
  { ma: 'USER', ten: 'Khách hàng', moTa: 'Cần cứu hộ xe' },
  { ma: 'THO', ten: 'Thợ cứu hộ', moTa: 'Nhận cuốc, sửa xe' },
];

export default function DangKyScreen() {
  const { dangKy } = useAuth();
  const [hoTen, setHoTen] = useState('');
  const [soDienThoai, setSoDienThoai] = useState('');
  const [matKhau, setMatKhau] = useState('');
  const [vaiTro, setVaiTro] = useState('USER');
  const [dangGui, setDangGui] = useState(false);
  const [loi, setLoi] = useState({});

  const gui = async () => {
    const loiMoi = {};
    if (hoTen.trim().length === 0) {
      loiMoi.hoTen = 'Nhập họ tên';
    }
    if (!SDT_HOP_LE.test(soDienThoai)) {
      loiMoi.soDienThoai = 'Số điện thoại gồm 10 chữ số, bắt đầu bằng 0';
    }
    if (matKhau.length < 8) {
      loiMoi.matKhau = 'Mật khẩu tối thiểu 8 ký tự';
    }
    setLoi(loiMoi);
    if (Object.keys(loiMoi).length > 0) {
      return;
    }
    setDangGui(true);
    try {
      await dangKy({ hoTen: hoTen.trim(), soDienThoai, matKhau, vaiTro });
    } catch (e) {
      const chiTiet = e.chiTiet ? `\n${Object.values(e.chiTiet).join('\n')}` : '';
      Alert.alert('Không đăng ký được', `${e.message}${chiTiet}`);
    } finally {
      setDangGui(false);
    }
  };

  return (
    <KeyboardAvoidingView style={styles.khung} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
      <ScrollView contentContainerStyle={styles.noiDung} keyboardShouldPersistTaps="handled">
        <Text style={styles.nhan}>Bạn là</Text>
        <View style={styles.hang}>
          {VAI_TRO.map((v) => (
            <TouchableOpacity
              key={v.ma}
              onPress={() => setVaiTro(v.ma)}
              style={[styles.lua, vaiTro === v.ma && styles.luaChon]}
              activeOpacity={0.8}
            >
              <Text style={[styles.luaTen, vaiTro === v.ma && styles.luaTenChon]}>{v.ten}</Text>
              <Text style={styles.luaMoTa}>{v.moTa}</Text>
            </TouchableOpacity>
          ))}
        </View>

        <TruongNhap nhan="Họ tên" value={hoTen} onChangeText={setHoTen} autoCapitalize="words" maxLength={100} loi={loi.hoTen} />
        <TruongNhap
          nhan="Số điện thoại"
          value={soDienThoai}
          onChangeText={setSoDienThoai}
          keyboardType="phone-pad"
          maxLength={10}
          loi={loi.soDienThoai}
        />
        <TruongNhap
          nhan="Mật khẩu (tối thiểu 8 ký tự)"
          value={matKhau}
          onChangeText={setMatKhau}
          secureTextEntry
          maxLength={72}
          loi={loi.matKhau}
        />
        {vaiTro === 'THO' ? (
          <Text style={styles.luuY}>
            Hồ sơ thợ cần được quản trị viên duyệt trước khi bạn nhận được cuốc.
          </Text>
        ) : null}
        <Nut nhan="Tạo tài khoản" onPress={gui} dangTai={dangGui} />
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  khung: { flex: 1, backgroundColor: mau.nen },
  noiDung: { padding: 24 },
  nhan: { fontSize: 14, fontWeight: '600', color: mau.chu, marginBottom: 8 },
  hang: { flexDirection: 'row', gap: 12, marginBottom: 18 },
  lua: {
    flex: 1,
    padding: 14,
    borderRadius: banKinh,
    borderWidth: 1,
    borderColor: mau.vien,
    backgroundColor: '#fff',
  },
  luaChon: { borderColor: mau.chinh, backgroundColor: '#fdecea' },
  luaTen: { fontWeight: '700', fontSize: 15, color: mau.chu },
  luaTenChon: { color: mau.chinh },
  luaMoTa: { fontSize: 12, color: mau.chuPhu, marginTop: 2 },
  luuY: { color: mau.cam, marginBottom: 14 },
});
