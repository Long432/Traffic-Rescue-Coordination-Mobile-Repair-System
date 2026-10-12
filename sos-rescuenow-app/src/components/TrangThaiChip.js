import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { mauTrangThai, tenTrangThai } from '../utils/nhan';

export default function TrangThaiChip({ trangThai }) {
  return (
    <View style={[styles.chip, { backgroundColor: mauTrangThai(trangThai) }]}>
      <Text style={styles.chu}>{tenTrangThai(trangThai)}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  chip: { alignSelf: 'flex-start', paddingHorizontal: 12, paddingVertical: 5, borderRadius: 999 },
  chu: { color: '#fff', fontWeight: '700', fontSize: 13 },
});
