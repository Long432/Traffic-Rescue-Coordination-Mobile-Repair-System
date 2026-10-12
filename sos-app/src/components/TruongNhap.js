import React from 'react';
import { StyleSheet, Text, TextInput, View } from 'react-native';
import { banKinh, mau } from '../theme';

/** O nhap lieu co nhan. Cac props khac (secureTextEntry, keyboardType...) duoc chuyen cho TextInput. */
export default function TruongNhap({ nhan, loi, ...props }) {
  return (
    <View style={styles.khung}>
      {nhan ? <Text style={styles.nhan}>{nhan}</Text> : null}
      <TextInput
        placeholderTextColor={mau.xam}
        autoCapitalize="none"
        style={[styles.o, loi ? styles.oLoi : null]}
        {...props}
      />
      {loi ? <Text style={styles.loi}>{loi}</Text> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  khung: { marginBottom: 14 },
  nhan: { fontSize: 14, fontWeight: '600', color: mau.chu, marginBottom: 6 },
  o: {
    borderWidth: 1,
    borderColor: mau.vien,
    borderRadius: banKinh,
    backgroundColor: '#fff',
    paddingHorizontal: 14,
    paddingVertical: 12,
    fontSize: 16,
    color: mau.chu,
  },
  oLoi: { borderColor: mau.loi },
  loi: { color: mau.loi, fontSize: 13, marginTop: 4 },
});
