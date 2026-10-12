import React, { useState } from 'react';
import { Modal, StyleSheet, Text, TextInput, View } from 'react-native';
import { banKinh, mau } from '../theme';
import Nut from './Nut';

/** Hop thoai nhap ly do (dung thay Alert.prompt vi Android khong co). */
export default function HopThoaiLyDo({ hienThi, tieuDe, goiY, batBuoc = true, onXacNhan, onDong }) {
  const [lyDo, setLyDo] = useState('');
  const thieu = batBuoc && lyDo.trim().length === 0;

  const dong = () => {
    setLyDo('');
    onDong();
  };
  const xacNhan = () => {
    const giaTri = lyDo.trim();
    setLyDo('');
    onXacNhan(giaTri);
  };

  return (
    <Modal visible={hienThi} transparent animationType="fade" onRequestClose={dong}>
      <View style={styles.nen}>
        <View style={styles.hop}>
          <Text style={styles.tieuDe}>{tieuDe}</Text>
          <TextInput
            value={lyDo}
            onChangeText={setLyDo}
            placeholder={goiY}
            placeholderTextColor={mau.xam}
            multiline
            maxLength={255}
            style={styles.o}
          />
          <View style={styles.hang}>
            <Nut nhan="Đóng" kieu="phu" onPress={dong} style={styles.nutNho} />
            <Nut nhan="Xác nhận" onPress={xacNhan} voHieu={thieu} style={styles.nutNho} />
          </View>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  nen: { flex: 1, backgroundColor: 'rgba(0,0,0,0.45)', justifyContent: 'center', padding: 24 },
  hop: { backgroundColor: '#fff', borderRadius: banKinh, padding: 20 },
  tieuDe: { fontSize: 18, fontWeight: '700', color: mau.chu, marginBottom: 12 },
  o: {
    borderWidth: 1,
    borderColor: mau.vien,
    borderRadius: banKinh,
    padding: 12,
    minHeight: 80,
    textAlignVertical: 'top',
    fontSize: 16,
    color: mau.chu,
  },
  hang: { flexDirection: 'row', gap: 12, marginTop: 16 },
  nutNho: { flex: 1 },
});
