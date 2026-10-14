import React from 'react';
import { Alert, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import * as api from '../api';
import ManHinhTai from '../components/ManHinhTai';
import Nut from '../components/Nut';
import { useAuth } from '../auth/AuthContext';
import { ThoProvider } from '../tho/ThoContext';
import { mau } from '../theme';
import DangKyScreen from '../screens/DangKyScreen';
import DangNhapScreen from '../screens/DangNhapScreen';
import AdminHomeScreen from '../screens/admin/AdminHomeScreen';
import KhachHomeScreen from '../screens/khach/KhachHomeScreen';
import LichSuScreen from '../screens/khach/LichSuScreen';
import TheoDoiCuocScreen from '../screens/khach/TheoDoiCuocScreen';
import ThoCuocScreen from '../screens/tho/ThoCuocScreen';
import ThoHomeScreen from '../screens/tho/ThoHomeScreen';

const Stack = createNativeStackNavigator();

const tuyChonChung = {
  headerTintColor: mau.chinh,
  headerTitleStyle: { fontWeight: '700' },
  contentStyle: { backgroundColor: mau.nen },
};

function NutTieuDe({ nhan, onPress }) {
  return (
    <TouchableOpacity onPress={onPress} style={styles.nutTieuDe}>
      <Text style={styles.chuNutTieuDe}>{nhan}</Text>
    </TouchableOpacity>
  );
}

function xacNhanDangXuat(thucHien) {
  Alert.alert('Đăng xuất', 'Bạn muốn đăng xuất khỏi tài khoản này?', [
    { text: 'Không', style: 'cancel' },
    { text: 'Đăng xuất', style: 'destructive', onPress: thucHien },
  ]);
}

function AuthStack() {
  return (
    <Stack.Navigator screenOptions={tuyChonChung}>
      <Stack.Screen name="DangNhap" component={DangNhapScreen} options={{ headerShown: false }} />
      <Stack.Screen name="DangKy" component={DangKyScreen} options={{ title: 'Tạo tài khoản' }} />
    </Stack.Navigator>
  );
}

function KhachStack() {
  const { dangXuat } = useAuth();
  return (
    <Stack.Navigator screenOptions={tuyChonChung}>
      <Stack.Screen
        name="KhachHome"
        component={KhachHomeScreen}
        options={({ navigation }) => ({
          title: 'SOS RescueNow',
          headerRight: () => (
            <View style={styles.hang}>
              <NutTieuDe nhan="Lịch sử" onPress={() => navigation.navigate('LichSu')} />
              <NutTieuDe nhan="Thoát" onPress={() => xacNhanDangXuat(dangXuat)} />
            </View>
          ),
        })}
      />
      <Stack.Screen name="TheoDoiCuoc" component={TheoDoiCuocScreen} options={{ title: 'Theo dõi yêu cầu' }} />
      <Stack.Screen name="LichSu" component={LichSuScreen} options={{ title: 'Lịch sử yêu cầu' }} />
    </Stack.Navigator>
  );
}

function ThoStack() {
  const { dangXuat } = useAuth();
  // Dang xuat: co gang tat online truoc (neu dang co cuoc thi server tu choi, van cho dang xuat).
  const thoat = async () => {
    try {
      await api.thoDatOnline(false);
    } catch (e) {
      // bo qua
    }
    dangXuat();
  };
  return (
    <ThoProvider>
      <Stack.Navigator screenOptions={tuyChonChung}>
        <Stack.Screen
          name="ThoHome"
          component={ThoHomeScreen}
          options={{
            title: 'Thợ cứu hộ',
            headerRight: () => <NutTieuDe nhan="Thoát" onPress={() => xacNhanDangXuat(thoat)} />,
          }}
        />
        <Stack.Screen
          name="ThoCuoc"
          component={ThoCuocScreen}
          options={{ title: 'Cuốc đang làm', headerBackVisible: false, gestureEnabled: false }}
        />
      </Stack.Navigator>
    </ThoProvider>
  );
}

function AdminStack() {
  const { dangXuat } = useAuth();
  return (
    <Stack.Navigator screenOptions={tuyChonChung}>
      <Stack.Screen
        name="AdminHome"
        component={AdminHomeScreen}
        options={{
          title: 'Quản trị - duyệt thợ',
          headerRight: () => <NutTieuDe nhan="Thoát" onPress={() => xacNhanDangXuat(dangXuat)} />,
        }}
      />
    </Stack.Navigator>
  );
}

function KhongHoTro() {
  const { dangXuat } = useAuth();
  return (
    <View style={styles.giua}>
      <Text style={styles.thongBao}>Vai trò tài khoản không được hỗ trợ trong ứng dụng này.</Text>
      <Nut nhan="Đăng xuất" kieu="phu" onPress={dangXuat} />
    </View>
  );
}

export default function AppNavigator() {
  const { phien, dangTai } = useAuth();
  if (dangTai) {
    return <ManHinhTai />;
  }

  let noiDung;
  if (!phien) {
    noiDung = <AuthStack />;
  } else if (phien.vaiTro === 'USER') {
    noiDung = <KhachStack />;
  } else if (phien.vaiTro === 'THO') {
    noiDung = <ThoStack />;
  } else if (phien.vaiTro === 'ADMIN') {
    noiDung = <AdminStack />;
  } else {
    noiDung = <KhongHoTro />;
  }
  return <NavigationContainer>{noiDung}</NavigationContainer>;
}

const styles = StyleSheet.create({
  hang: { flexDirection: 'row' },
  nutTieuDe: { paddingHorizontal: 8, paddingVertical: 6 },
  chuNutTieuDe: { color: mau.chinh, fontWeight: '700', fontSize: 15 },
  giua: { flex: 1, alignItems: 'center', justifyContent: 'center', padding: 24, gap: 16, backgroundColor: mau.nen },
  thongBao: { textAlign: 'center', fontSize: 16, color: mau.chu },
});
