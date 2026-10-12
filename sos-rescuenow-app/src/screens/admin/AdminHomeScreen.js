import React, { useCallback, useState } from 'react';
import { Alert, FlatList, RefreshControl, StyleSheet, Text, View } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import * as api from '../../api';
import ManHinhTai from '../../components/ManHinhTai';
import Nut from '../../components/Nut';
import The from '../../components/The';
import { mau } from '../../theme';

export default function AdminHomeScreen() {
  const [ds, setDs] = useState(null);
  const [chiChuaDuyet, setChiChuaDuyet] = useState(true);
  const [loi, setLoi] = useState(null);
  const [dangLamMoi, setDangLamMoi] = useState(false);
  const [dangDuyet, setDangDuyet] = useState(null);

  const tai = useCallback(async () => {
    try {
      setDs(await api.adminDanhSachTho(chiChuaDuyet));
      setLoi(null);
    } catch (e) {
      setLoi(e.message);
    }
  }, [chiChuaDuyet]);

  useFocusEffect(
    useCallback(() => {
      tai();
    }, [tai]),
  );

  const lamMoi = async () => {
    setDangLamMoi(true);
    await tai();
    setDangLamMoi(false);
  };

  const duyet = (tho) => {
    Alert.alert('Duyệt thợ?', `Cho phép ${tho.hoTen} (${tho.soDienThoai}) nhận mọi loại sự cố?`, [
      { text: 'Không', style: 'cancel' },
      {
        text: 'Duyệt',
        onPress: async () => {
          setDangDuyet(tho.id);
          try {
            await api.adminDuyetTho(tho.id);
            await tai();
          } catch (e) {
            Alert.alert('Không duyệt được', e.message);
          } finally {
            setDangDuyet(null);
          }
        },
      },
    ]);
  };

  if (ds === null && !loi) {
    return <ManHinhTai />;
  }

  return (
    <View style={styles.khung}>
      <View style={styles.boLoc}>
        <Nut
          nhan="Chờ duyệt"
          kieu={chiChuaDuyet ? 'chinh' : 'phu'}
          onPress={() => setChiChuaDuyet(true)}
          style={styles.nutLoc}
        />
        <Nut
          nhan="Tất cả thợ"
          kieu={chiChuaDuyet ? 'phu' : 'chinh'}
          onPress={() => setChiChuaDuyet(false)}
          style={styles.nutLoc}
        />
      </View>
      <FlatList
        data={ds || []}
        keyExtractor={(t) => String(t.id)}
        contentContainerStyle={styles.noiDung}
        refreshControl={<RefreshControl refreshing={dangLamMoi} onRefresh={lamMoi} />}
        ListEmptyComponent={
          <Text style={styles.trong}>
            {loi || (chiChuaDuyet ? 'Không có thợ nào đang chờ duyệt.' : 'Chưa có thợ nào đăng ký.')}
          </Text>
        }
        renderItem={({ item }) => (
          <The>
            <Text style={styles.ten}>{item.hoTen}</Text>
            <Text style={styles.phu}>
              Mã #{item.id} · {item.soDienThoai}
            </Text>
            <Text style={styles.phu}>
              {item.daDuyet ? 'Đã duyệt' : 'Chưa duyệt'} · {item.online ? 'đang online' : 'offline'}
              {item.dangBan ? ' · đang bận' : ''} · {Number(item.diemTrungBinh).toFixed(2)} ★
            </Text>
            {!item.daDuyet ? (
              <Nut
                nhan="Duyệt thợ này"
                kieu="xanh"
                onPress={() => duyet(item)}
                dangTai={dangDuyet === item.id}
                style={styles.nutDuyet}
              />
            ) : null}
          </The>
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  khung: { flex: 1 },
  boLoc: { flexDirection: 'row', gap: 12, padding: 16, paddingBottom: 0 },
  nutLoc: { flex: 1, minHeight: 42 },
  noiDung: { padding: 16, flexGrow: 1 },
  ten: { fontSize: 17, fontWeight: '700', color: mau.chu },
  phu: { color: mau.chuPhu, marginTop: 3 },
  nutDuyet: { marginTop: 12 },
  trong: { textAlign: 'center', color: mau.chuPhu, marginTop: 40 },
});
