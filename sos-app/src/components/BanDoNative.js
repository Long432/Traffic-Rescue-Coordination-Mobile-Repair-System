import React, { useEffect, useRef } from 'react';
import MapView, { Marker } from 'react-native-maps';

const MAC_DINH = { latitude: 10.7769, longitude: 106.7009 };

/** Ban do goc (react-native-maps). Xem KIEU_BAN_DO trong config.js.
 *
 * Ban do hien cac diem. diem: [{ id, latitude, longitude, tieuDe, mau }] (bo qua diem thieu toa do).
 * Tu dong thu phong cho vua tat ca cac diem.
 */
export default function BanDoNative({ diem, style }) {
  const mapRef = useRef(null);
  const hopLe = (diem || []).filter(
    (d) => d && typeof d.latitude === 'number' && typeof d.longitude === 'number',
  );
  const khoa = hopLe.map((d) => `${d.id}:${d.latitude.toFixed(5)},${d.longitude.toFixed(5)}`).join('|');

  useEffect(() => {
    const ban = mapRef.current;
    if (!ban || hopLe.length === 0) {
      return;
    }
    if (hopLe.length === 1) {
      ban.animateToRegion(
        { latitude: hopLe[0].latitude, longitude: hopLe[0].longitude, latitudeDelta: 0.01, longitudeDelta: 0.01 },
        300,
      );
    } else {
      ban.fitToCoordinates(
        hopLe.map((d) => ({ latitude: d.latitude, longitude: d.longitude })),
        { edgePadding: { top: 70, right: 70, bottom: 70, left: 70 }, animated: true },
      );
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [khoa]);

  const dauTien = hopLe[0] || MAC_DINH;
  return (
    <MapView
      ref={mapRef}
      style={style}
      initialRegion={{
        latitude: dauTien.latitude,
        longitude: dauTien.longitude,
        latitudeDelta: 0.02,
        longitudeDelta: 0.02,
      }}
    >
      {hopLe.map((d) => (
        <Marker
          key={d.id}
          coordinate={{ latitude: d.latitude, longitude: d.longitude }}
          title={d.tieuDe}
          pinColor={d.mau}
        />
      ))}
    </MapView>
  );
}
