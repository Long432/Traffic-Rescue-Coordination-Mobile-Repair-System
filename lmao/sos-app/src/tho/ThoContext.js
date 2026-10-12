import React, { createContext, useCallback, useContext, useEffect, useMemo, useRef, useState } from 'react';
import * as Location from 'expo-location';
import * as api from '../api';
import { xinQuyenViTri } from '../utils/viTri';

const CHU_KY_GUI_VI_TRI_MS = 5000;

const ThoContext = createContext(null);

/**
 * Giu trang thai online cua tho va TU DONG gui vi tri len server moi 5 giay khi online.
 * (Hien chay khi app dang mo. Chay nen khi tat man hinh la module sau, can ban development build.)
 */
export function ThoProvider({ children }) {
  const [hoSo, setHoSo] = useState(null); // { online, dangBan, daDuyet, diemTrungBinh, tiLeNhanCuoc }
  const [viTri, setViTri] = useState(null); // { latitude, longitude }
  const viTriRef = useRef(null);

  const taiHoSo = useCallback(async () => {
    const h = await api.thoHoSo();
    setHoSo(h);
    return h;
  }, []);

  const datOnline = useCallback(
    async (batOn) => {
      if (batOn && !(await xinQuyenViTri())) {
        throw new Error('Bạn cần cấp quyền vị trí để nhận cuốc.');
      }
      await api.thoDatOnline(batOn);
      await taiHoSo();
    },
    [taiHoSo],
  );

  const dangOnline = Boolean(hoSo && hoSo.online);

  useEffect(() => {
    if (!dangOnline) {
      return undefined;
    }
    let huy = false;
    let theoDoi = null;

    const guiLenServer = async () => {
      const v = viTriRef.current;
      if (!v) {
        return;
      }
      try {
        await api.thoGuiViTri(v.latitude, v.longitude);
      } catch (e) {
        // mat mang tam thoi: lan gui sau (5 giay nua) se bu lai
      }
    };

    const capNhat = (toaDo) => {
      viTriRef.current = { latitude: toaDo.latitude, longitude: toaDo.longitude };
      setViTri(viTriRef.current);
    };

    (async () => {
      try {
        const { status } = await Location.getForegroundPermissionsAsync();
        if (status !== 'granted' || huy) {
          return;
        }
        try {
          const dau = await Location.getCurrentPositionAsync({ accuracy: Location.Accuracy.Balanced });
          if (!huy) {
            capNhat(dau.coords);
            await guiLenServer(); // gui ngay, khong doi chu ky dau tien
          }
        } catch (e) {
          // chua co vi tri ngay: watchPosition ben duoi se cap nhat khi co
        }
        const sub = await Location.watchPositionAsync(
          { accuracy: Location.Accuracy.Balanced, timeInterval: 3000, distanceInterval: 5 },
          (viTriMoi) => capNhat(viTriMoi.coords),
        );
        if (huy) {
          sub.remove();
        } else {
          theoDoi = sub;
        }
      } catch (e) {
        // khong theo doi duoc vi tri: man hinh se hien "dang lay vi tri"
      }
    })();

    const hen = setInterval(guiLenServer, CHU_KY_GUI_VI_TRI_MS);
    return () => {
      huy = true;
      clearInterval(hen);
      if (theoDoi) {
        theoDoi.remove();
      }
    };
  }, [dangOnline]);

  const giaTri = useMemo(
    () => ({ hoSo, viTri, taiHoSo, datOnline }),
    [hoSo, viTri, taiHoSo, datOnline],
  );
  return <ThoContext.Provider value={giaTri}>{children}</ThoContext.Provider>;
}

export function useTho() {
  const ctx = useContext(ThoContext);
  if (!ctx) {
    throw new Error('useTho phai nam trong ThoProvider');
  }
  return ctx;
}
