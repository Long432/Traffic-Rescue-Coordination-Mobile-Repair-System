import React, { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import * as SecureStore from 'expo-secure-store';
import * as api from '../api';
import { datToken, datXuLyHetPhien } from '../api/client';

const KHOA_LUU = 'sos_phien';
const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [phien, setPhien] = useState(null); // { accessToken, nguoiDungId, hoTen, vaiTro }
  const [dangTai, setDangTai] = useState(true);

  const dangXuat = useCallback(async () => {
    datToken(null);
    setPhien(null);
    try {
      await SecureStore.deleteItemAsync(KHOA_LUU);
    } catch (e) {
      // khong xoa duoc kho luu tru: bo qua, phien trong bo nho da bi xoa
    }
  }, []);

  // Khoi dong: doc phien da luu; dang ky xu ly khi server bao het han token.
  useEffect(() => {
    datXuLyHetPhien(() => {
      dangXuat();
    });
    (async () => {
      try {
        const raw = await SecureStore.getItemAsync(KHOA_LUU);
        if (raw) {
          const p = JSON.parse(raw);
          datToken(p.accessToken);
          setPhien(p);
        }
      } catch (e) {
        // du lieu luu bi hong: coi nhu chua dang nhap
      } finally {
        setDangTai(false);
      }
    })();
  }, [dangXuat]);

  const luuPhien = useCallback(async (ketQua) => {
    const p = {
      accessToken: ketQua.accessToken,
      nguoiDungId: ketQua.nguoiDungId,
      hoTen: ketQua.hoTen,
      vaiTro: ketQua.vaiTro,
    };
    datToken(p.accessToken);
    setPhien(p);
    try {
      await SecureStore.setItemAsync(KHOA_LUU, JSON.stringify(p));
    } catch (e) {
      // khong luu duoc: van dung duoc trong phien nay
    }
  }, []);

  const dangNhap = useCallback(
    async (soDienThoai, matKhau) => luuPhien(await api.dangNhap({ soDienThoai, matKhau })),
    [luuPhien],
  );

  const dangKy = useCallback(async (duLieu) => luuPhien(await api.dangKy(duLieu)), [luuPhien]);

  const giaTri = useMemo(
    () => ({ phien, dangTai, dangNhap, dangKy, dangXuat }),
    [phien, dangTai, dangNhap, dangKy, dangXuat],
  );
  return <AuthContext.Provider value={giaTri}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuth phai nam trong AuthProvider');
  }
  return ctx;
}
