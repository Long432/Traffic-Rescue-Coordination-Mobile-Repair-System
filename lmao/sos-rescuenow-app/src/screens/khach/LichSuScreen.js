import React, { useCallback, useState } from 'react';
import { FlatList, RefreshControl, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import * as api from '../../api';
import ManHinhTai from '../../components/ManHinhTai';
import TrangThaiChip from '../../components/TrangThaiChip';
import { banKinh, mau } from '../../theme';
import { formatTien } from '../../utils/nhan';

function formatNgay(iso) {
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) {
    return '';
  }
  const hai = (n) => String(n).padStart(2, '0');
  return `${hai(d.getHours())}:${hai(d.getMinutes())} ${hai(d.getDate())}/${hai(d.getMonth() + 1)}/${d.getFullYear()}`;
}

export default function LichSuScreen({ navigation }) {
  const [ds, setDs] = useState(null);
  const [loi, setLoi] = useState(null);
  const [dangLamMoi, setDangLamMoi] = useState(false);

  const tai = useCallback(async () => {
    try {
      setDs(await api.cuocCuaToi());
      setLoi(null);
    } catch (e) {
      setLoi(e.message);
    }
  }, []);

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

  if (ds === null && !loi) {
    return <ManHinhTai />;
  }

  return (
    <FlatList
      data={ds || []}
      keyExtractor={(c) => String(c.id)}
      contentContainerStyle={styles.noiDung}
      refreshControl={<RefreshControl refreshing={dangLamMoi} onRefresh={lamMoi} />}
      ListEmptyComponent={
        <Text style={styles.trong}>{loi ? loi : 'Bạn chưa có yêu cầu cứu hộ nào.'}</Text>
      }
      renderItem={({ item }) => (
        <TouchableOpacity
          style={styles.the}
          activeOpacity={0.8}
          onPress={() => navigation.navigate('TheoDoiCuoc', { cuocId: item.id })}
        >
          <View style={styles.hang}>
            <Text style={styles.ma}>Yêu cầu #{item.id}</Text>
            <Text style={styles.tien}>{formatTien(item.tongTien ?? item.giaUocTinh)}</Text>
          </View>
          <TrangThaiChip trangThai={item.trangThai} />
          <Text style={styles.ngay}>{formatNgay(item.ngayTao)}</Text>
        </TouchableOpacity>
      )}
    />
  );
}

const styles = StyleSheet.create({
  noiDung: { padding: 16, flexGrow: 1 },
  the: {
    backgroundColor: '#fff',
    borderRadius: banKinh,
    borderWidth: 1,
    borderColor: mau.vien,
    padding: 14,
    marginBottom: 10,
  },
  hang: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 8 },
  ma: { fontWeight: '700', color: mau.chu, fontSize: 16 },
  tien: { fontWeight: '700', color: mau.chu },
  ngay: { color: mau.chuPhu, marginTop: 8, fontSize: 13 },
  trong: { textAlign: 'center', color: mau.chuPhu, marginTop: 40 },
});
