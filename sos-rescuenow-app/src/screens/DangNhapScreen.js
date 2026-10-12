import React, { useState } from 'react';
import { Alert, KeyboardAvoidingView, Platform, ScrollView, StyleSheet, Text, View } from 'react-native';
import Nut from '../components/Nut';
import TruongNhap from '../components/TruongNhap';
import { useAuth } from '../auth/AuthContext';
import { mau } from '../theme';
import { SDT_HOP_LE } from '../utils/nhan';

export default function DangNhapScreen({ navigation }) {
  const { dangNhap } = useAuth();
  const [soDienThoai, setSoDienThoai] = useState('');
  const [matKhau, setMatKhau] = useState('');
  const [dangGui, setDangGui] = useState(false);
  const [loi, setLoi] = useState({});

  const gui = async () => {
    const loiMoi = {};
    if (!SDT_HOP_LE.test(soDienThoai)) {
      loiMoi.soDienThoai = 'Số điện thoại gồm 10 chữ số, bắt đầu bằng 0';
    }
    if (matKhau.length === 0) {
      loiMoi.matKhau = 'Nhập mật khẩu';
    }
    setLoi(loiMoi);
    if (Object.keys(loiMoi).length > 0) {
      return;
    }
    setDangGui(true);
    try {
      await dangNhap(soDienThoai, matKhau);
    } catch (e) {
      Alert.alert('Không đăng nhập được', e.message);
    } finally {
      setDangGui(false);
    }
  };

  return (
    <KeyboardAvoidingView style={styles.khung} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
      <ScrollView contentContainerStyle={styles.noiDung} keyboardShouldPersistTaps="handled">
        <Text style={styles.logo}>SOS RescueNow</Text>
        <Text style={styles.moTa}>Cứu hộ xe lưu động, đến tận nơi</Text>
        <View style={styles.form}>
          <TruongNhap
            nhan="Số điện thoại"
            value={soDienThoai}
            onChangeText={setSoDienThoai}
            keyboardType="phone-pad"
            maxLength={10}
            loi={loi.soDienThoai}
          />
          <TruongNhap
            nhan="Mật khẩu"
            value={matKhau}
            onChangeText={setMatKhau}
            secureTextEntry
            loi={loi.matKhau}
          />
          <Nut nhan="Đăng nhập" onPress={gui} dangTai={dangGui} />
          <Nut
            nhan="Chưa có tài khoản? Đăng ký"
            kieu="phu"
            onPress={() => navigation.navigate('DangKy')}
            style={styles.nutPhu}
          />
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  khung: { flex: 1, backgroundColor: mau.nen },
  noiDung: { flexGrow: 1, justifyContent: 'center', padding: 24 },
  logo: { fontSize: 32, fontWeight: '800', color: mau.chinh, textAlign: 'center' },
  moTa: { textAlign: 'center', color: mau.chuPhu, marginTop: 4, marginBottom: 32 },
  form: { width: '100%' },
  nutPhu: { marginTop: 12 },
});
