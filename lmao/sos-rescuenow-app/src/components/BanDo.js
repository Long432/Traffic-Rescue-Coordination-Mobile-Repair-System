import React from 'react';
import { KIEU_BAN_DO } from '../config';

/**
 * Ban do dung chung. diem: [{ id, latitude, longitude, tieuDe, mau }].
 * Chon cach ve theo KIEU_BAN_DO trong src/config.js ('osm' hoac 'native').
 */
export default function BanDo(props) {
  if (KIEU_BAN_DO === 'native') {
    const BanDoNative = require('./BanDoNative').default;
    return <BanDoNative {...props} />;
  }
  const BanDoOSM = require('./BanDoOSM').default;
  return <BanDoOSM {...props} />;
}
