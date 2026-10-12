import React from 'react';
import { StyleSheet, View } from 'react-native';
import { banKinh, mau } from '../theme';

export default function The({ children, style }) {
  return <View style={[styles.the, style]}>{children}</View>;
}

const styles = StyleSheet.create({
  the: {
    backgroundColor: mau.the,
    borderRadius: banKinh,
    padding: 16,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: mau.vien,
  },
});
