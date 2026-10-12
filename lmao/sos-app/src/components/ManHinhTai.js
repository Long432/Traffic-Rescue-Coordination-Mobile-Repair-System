import React from 'react';
import { ActivityIndicator, StyleSheet, Text, View } from 'react-native';
import { mau } from '../theme';

export default function ManHinhTai({ noiDung }) {
  return (
    <View style={styles.khung}>
      <ActivityIndicator size="large" color={mau.chinh} />
      {noiDung ? <Text style={styles.chu}>{noiDung}</Text> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  khung: { flex: 1, alignItems: 'center', justifyContent: 'center', backgroundColor: mau.nen },
  chu: { marginTop: 12, color: mau.chuPhu },
});
