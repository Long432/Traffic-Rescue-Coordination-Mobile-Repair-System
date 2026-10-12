import React, { useCallback, useEffect, useRef } from 'react';
import { StyleSheet, View } from 'react-native';
import { WebView } from 'react-native-webview';

// Trang HTML chua ban do Leaflet + o gach OpenStreetMap. Can Internet de tai thu vien va o gach.
// Dung circleMarker + textContent (khong dung HTML) de ten tho khong the chen ma vao trang.
const HTML = `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<style>
  html, body, #map { height: 100%; margin: 0; padding: 0; }
  .nhan { font: 600 12px sans-serif; }
</style>
</head>
<body>
<div id="map"></div>
<script>
  var map = L.map('map').setView([10.7769, 106.7009], 14);
  L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; OpenStreetMap'
  }).addTo(map);
  var markers = {};
  function capNhatDiem(ds) {
    var thay = {};
    var khung = [];
    ds.forEach(function (d) {
      thay[d.id] = true;
      var p = [d.latitude, d.longitude];
      khung.push(p);
      if (markers[d.id]) {
        markers[d.id].setLatLng(p);
      } else {
        var nhan = document.createElement('span');
        nhan.className = 'nhan';
        nhan.textContent = d.tieuDe || '';
        markers[d.id] = L.circleMarker(p, {
          radius: 11, color: '#ffffff', weight: 3, fillColor: d.mau, fillOpacity: 1
        }).addTo(map).bindTooltip(nhan, { permanent: true, direction: 'top', offset: [0, -10] });
      }
    });
    Object.keys(markers).forEach(function (id) {
      if (!thay[id]) { map.removeLayer(markers[id]); delete markers[id]; }
    });
    if (khung.length === 1) {
      map.setView(khung[0], 16);
    } else if (khung.length > 1) {
      map.fitBounds(khung, { padding: [60, 60], maxZoom: 17 });
    }
  }
</script>
</body>
</html>`;

/**
 * Ban do OpenStreetMap trong WebView (khong can khoa API). diem: [{ id, latitude, longitude, tieuDe, mau }].
 * Tu dong cap nhat va thu phong vua tat ca cac diem.
 */
export default function BanDoOSM({ diem, style }) {
  const webRef = useRef(null);
  const daTai = useRef(false);

  const hopLe = (diem || []).filter(
    (d) => d && typeof d.latitude === 'number' && typeof d.longitude === 'number',
  );
  const duLieu = JSON.stringify(
    hopLe.map((d) => ({ id: String(d.id), latitude: d.latitude, longitude: d.longitude, tieuDe: d.tieuDe, mau: d.mau })),
  );

  const gui = useCallback(() => {
    if (webRef.current && daTai.current) {
      // JSON hop le cung la bieu thuc JavaScript hop le; kiem tra ham da co (phong khi chua tai duoc thu vien).
      webRef.current.injectJavaScript(`if (typeof capNhatDiem === 'function') { capNhatDiem(${duLieu}); } true;`);
    }
  }, [duLieu]);

  useEffect(() => {
    gui();
  }, [gui]);

  return (
    <View style={[styles.khung, style]}>
      <WebView
        ref={webRef}
        style={styles.web}
        originWhitelist={['*']}
        source={{ html: HTML, baseUrl: 'https://sos-rescuenow.example' }}
        javaScriptEnabled
        domStorageEnabled
        nestedScrollEnabled
        onLoadEnd={() => {
          daTai.current = true;
          gui();
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  khung: { overflow: 'hidden', backgroundColor: '#e5e7eb' },
  web: { flex: 1, backgroundColor: 'transparent' },
});
