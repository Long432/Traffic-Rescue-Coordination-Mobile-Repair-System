import React from 'react';
import { StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { mau } from '../theme';

/** Hang 5 ngoi sao. Co onChon thi bam duoc de chon; khong co thi chi hien thi. */
export default function ChonSao({ giaTri, onChon }) {
  return (
    <View style={styles.hang}>
      {[1, 2, 3, 4, 5].map((n) => (
        <TouchableOpacity key={n} disabled={!onChon} onPress={() => onChon && onChon(n)} activeOpacity={0.7}>
          <Text style={[styles.sao, n <= giaTri ? styles.saoDay : styles.saoRong]}>★</Text>
        </TouchableOpacity>
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  hang: { flexDirection: 'row', justifyContent: 'center', marginVertical: 8 },
  sao: { fontSize: 38, marginHorizontal: 4 },
  saoDay: { color: '#f5a623' },
  saoRong: { color: mau.vien },
});
