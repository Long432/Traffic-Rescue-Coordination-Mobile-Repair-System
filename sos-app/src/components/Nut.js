import React from 'react';
import { ActivityIndicator, StyleSheet, Text, TouchableOpacity } from 'react-native';
import { banKinh, mau } from '../theme';

/** Nut bam. kieu: 'chinh' (do) | 'xanh' | 'phu' (vien) | 'nguyHiem'. */
export default function Nut({ nhan, onPress, kieu = 'chinh', dangTai = false, voHieu = false, style }) {
  const khoa = voHieu || dangTai;
  return (
    <TouchableOpacity
      onPress={onPress}
      disabled={khoa}
      activeOpacity={0.8}
      style={[styles.nut, styles[kieu], khoa && styles.khoa, style]}
    >
      {dangTai ? (
        <ActivityIndicator color={kieu === 'phu' ? mau.chinh : '#fff'} />
      ) : (
        <Text style={[styles.chu, kieu === 'phu' && styles.chuPhu]}>{nhan}</Text>
      )}
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  nut: {
    minHeight: 48,
    paddingVertical: 12,
    paddingHorizontal: 16,
    borderRadius: banKinh,
    alignItems: 'center',
    justifyContent: 'center',
  },
  chinh: { backgroundColor: mau.chinh },
  xanh: { backgroundColor: mau.xanh },
  nguyHiem: { backgroundColor: mau.loi },
  phu: { backgroundColor: '#fff', borderWidth: 1, borderColor: mau.chinh },
  khoa: { opacity: 0.5 },
  chu: { color: '#fff', fontWeight: '700', fontSize: 16, textAlign: 'center' },
  chuPhu: { color: mau.chinh },
});
