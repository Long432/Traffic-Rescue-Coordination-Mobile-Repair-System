# SOS RescueNow – Ứng dụng di động (Module 4)

React Native + Expo (**SDK 57**, khớp Expo Go 57), JavaScript. Một ứng dụng, hai vai trò: **Khách hàng** và **Thợ cứu hộ**
(tự chọn theo tài khoản khi đăng nhập).

## Ứng dụng làm được gì

**Quản trị viên:** xem danh sách thợ, duyệt thợ mới (một nút bấm).

**Khách hàng:** đăng ký/đăng nhập (lưu phiên an toàn) · chọn loại sự cố, tự lấy vị trí và địa chỉ · gửi SOS ·
theo dõi trên bản đồ (vị trí thợ cập nhật 3 giây/lần, khoảng cách, giá) · hủy yêu cầu · đánh giá thợ bằng sao sau khi hoàn thành · xem lịch sử.

**Thợ:** bật/tắt "Sẵn sàng nhận cuốc" · tự gửi vị trí 5 giây/lần khi online · danh sách cuốc quanh mình tự cập nhật ·
nhận cuốc · đi qua các bước (bắt đầu đi → đã đến nơi → sửa → tính tiền → hoàn thành) · trả cuốc kèm lý do ·
mở lại app vẫn tiếp tục cuốc đang làm.

**Giới hạn hiện tại (để module sau):** app phải đang mở mới gửi được vị trí (chạy nền cần bản development build);
cập nhật bằng cách hỏi lại server vài giây một lần (WebSocket là module sau).

---

## Cài đặt từng bước (Windows)

### Bước 1. Kiểm tra Node.js
```powershell
node -v
```
Cần **v20.19 trở lên** (nên dùng bản LTS). Chưa có thì tải ở https://nodejs.org rồi mở lại PowerShell.

### Bước 2. Cài Expo Go trên điện thoại
Cài **Expo Go** từ Google Play hoặc App Store. Điện thoại và máy tính phải **cùng một mạng Wi‑Fi**.

> **Quan trọng:** mỗi bản Expo Go chỉ chạy đúng **một** phiên bản SDK. Bản Expo Go của bạn là 57 nên dự án
> phải là **SDK 57** (hướng dẫn này làm theo SDK 57). Expo Go sẽ tự lên SDK 58 sau khi SDK 58 phát hành chính thức;
> khi đó nếu app báo "incompatible", xem mục *Gặp lỗi* bên dưới. Bạn có thể tắt tự cập nhật của Expo Go trong lúc làm đồ án.

### Bước 3. Tạo dự án Expo trống
```powershell
cd D:\project\lmao
npx create-expo-app@latest sos-app --template blank@sdk-57
cd sos-app
```
Nếu lệnh báo không tìm thấy template `blank@sdk-57`, dùng `npx create-expo-app@latest sos-app --template blank`.

Sau đó **kiểm tra hai thứ**:
1. Mở `package.json`: dòng `"expo"` phải bắt đầu bằng `~57`. Nếu không, chạy `npx expo install expo@^57.0.0 --fix`.
2. Trong thư mục `sos-app` phải có file **`App.js`** và dòng `"main": "index.js"` trong `package.json`.
   Nếu template của bạn khác (ví dụ có thư mục `app/` và `"main": "expo-router/entry"`), cứ chép tiếp ở Bước 5
   (gói của mình có sẵn `index.js`), rồi sửa dòng `"main"` thành `"index.js"` và **xóa thư mục `app`** nếu có.

### Bước 4. Cài các thư viện cần dùng
```powershell
npx expo install "@react-navigation/native@^7" "@react-navigation/native-stack@^7" react-native-screens react-native-safe-area-context expo-secure-store expo-location react-native-webview react-native-maps
```
- Dùng `npx expo install` (không phải `npm install`) để Expo chọn đúng phiên bản hợp với SDK 57.
- **Phải ghi `@^7` cho React Navigation**: bản 8 đang thử nghiệm và **Expo Go không hỗ trợ**.

### Bước 5. Chép mã nguồn vào dự án
Giải nén gói `sos-rescuenow-app.zip`, rồi chép **`App.js`**, **`index.js`** (ghi đè file cũ) và cả thư mục **`src`**
vào thư mục `D:\project\lmao\sos-app`.

### Bước 6. Cho điện thoại gọi được server
1. Lấy địa chỉ IP của máy tính: `ipconfig` → dòng **IPv4 Address** của Wi‑Fi (ví dụ `192.168.1.23`).
2. Mở `src/config.js` và sửa dòng `API_URL` thành `http://<IP-của-bạn>:8080`.
3. Mở cổng 8080 trong tường lửa Windows (mở PowerShell **bằng quyền Administrator**):
   ```powershell
   New-NetFirewallRule -DisplayName "SOS backend 8080" -Direction Inbound -Protocol TCP -LocalPort 8080 -Action Allow -Profile Any
   ```
   (Xóa khi không cần nữa: `Remove-NetFirewallRule -DisplayName "SOS backend 8080"`.)
4. Bật backend (`mvn spring-boot:run` ở thư mục backend, cần đủ ba biến môi trường).
5. **Thử trước trên trình duyệt điện thoại:** mở `http://<IP-của-bạn>:8080/api/loai-su-co`.
   Thấy chữ JSON có `"maLoi":"CHUA_XAC_THUC"` là **thông** (server từ chối vì chưa đăng nhập, đúng như mong đợi).
   Không mở được thì lỗi nằm ở mạng/tường lửa, chưa liên quan đến app.

### Bước 7. Chạy app
```powershell
npx expo start
```
Quét mã QR: Android mở ứng dụng **Expo Go** rồi chọn *Scan QR code*; iPhone dùng ứng dụng **Camera**.
iPhone có thể hỏi quyền "mạng cục bộ" cho Expo Go, hãy chọn **Cho phép**.

---

## Kịch bản thử (cần 2 thiết bị: khách và thợ)

Thợ có thể là điện thoại thứ hai, máy ảo Android, hoặc điện thoại của bạn cùng nhóm.

1. **Máy A (khách):** Đăng ký → "Khách hàng". **Máy B (thợ):** Đăng ký → "Thợ cứu hộ".
2. Máy B hiện thông báo "Hồ sơ thợ chưa được duyệt". **Duyệt thợ bằng tài khoản admin:** chạy backend với
   `ADMIN_SDT` và `ADMIN_MAT_KHAU` (xem README backend, mục 9.1), đăng nhập admin trên một máy (hoặc đăng xuất rồi đăng nhập
   admin trên máy A), vào màn hình **Quản trị - duyệt thợ** và bấm **Duyệt thợ này**. Sau đó ở máy B, **thoát ra vào lại**
   màn hình thợ để tải hồ sơ mới, bật công tắc **Sẵn sàng nhận cuốc** và cho phép quyền vị trí.
   (Cách cũ bằng SQL `04_duyet_tho_thu_nghiem.sql` vẫn dùng được.)
3. Máy A: chọn loại sự cố → **GỬI SOS**. Màn hình theo dõi hiện "Đang tìm thợ".
4. Máy B: cuốc xuất hiện trong danh sách trong vài giây → **NHẬN CUỐC**.
5. Máy A: thấy ghim xanh của thợ trên bản đồ và khoảng cách cập nhật liên tục.
6. Máy B: **Bắt đầu đi tới khách** → **Tôi đã đến nơi** (chỉ bấm được khi thợ cách khách **≤ 100 m**;
   hai máy để cạnh nhau là đủ) → **Bắt đầu sửa** → **Sửa xong, tính tiền** → **Đã thu tiền mặt, hoàn thành**.
7. Khi cuốc hoàn thành, máy A hiện **5 ngôi sao**: chọn số sao, viết nhận xét rồi **Gửi đánh giá**. Máy B (màn hình thợ)
   hiện điểm trung bình mới ở dòng "Đánh giá của khách". Điểm này ảnh hưởng thứ tự xếp hạng thợ ở các cuốc sau.
8. Thử thêm: khách **Hủy yêu cầu** khi thợ đang đến; thợ **Trả cuốc** (cuốc quay về tìm thợ khác);
   không bật thợ nào và chờ 2 phút để thấy "Không tìm được thợ".

Dùng **máy ảo Android**: đặt vị trí trong *Extended controls → Location* (máy ảo không có GPS thật).

---

## Gặp lỗi?

| Hiện tượng | Cách xử lý |
|---|---|
| "Project is incompatible with this version of Expo Go" | Expo Go và dự án khác SDK. Nếu Expo Go đã lên SDK 58: nâng dự án bằng `npx expo install expo@^58.0.0 --fix`, hoặc (Android) vào https://expo.dev/go chọn **SDK 57** để tải lại đúng bản Expo Go. |
| "Không kết nối được máy chủ" | Sai `API_URL`, khác mạng Wi‑Fi, chưa mở tường lửa, hoặc backend chưa chạy. Làm lại Bước 6, nhất là bước thử bằng trình duyệt điện thoại. |
| QR không kết nối được dù cùng Wi‑Fi | Mạng chặn kết nối nội bộ. Thử `npx expo start --tunnel` (sẽ hỏi cài thêm gói, chọn đồng ý). |
| Bản đồ trắng hoặc không có nền | Bản đồ mặc định (OpenStreetMap) cần Internet để tải thư viện và ô bản đồ; đợi vài giây. |
| Bản đồ **đen** khi đổi sang `KIEU_BAN_DO = 'native'` (Android) | Lỗi đã được báo cho Expo: bản đồ Google trong Expo Go SDK 57 không được cấp quyền. Giữ `KIEU_BAN_DO = 'osm'` trong `src/config.js`. Dùng được bản đồ gốc khi đóng gói ứng dụng thật với khóa Google Maps của bạn. |
| Muốn chỉ đường cho thợ | Bấm "Chỉ đường (Google Maps)" trên màn hình cuốc, nó mở ứng dụng Google Maps với đường đi tới khách. |
| "Bạn cần cấp quyền vị trí" | Vào Cài đặt điện thoại → Expo Go → Vị trí → cho phép khi dùng ứng dụng. |
| Bấm "Tôi đã đến nơi" báo "chưa đến gần vị trí khách" | Đúng quy tắc: cần ≤ 100 m. Đưa hai máy lại gần nhau và đợi vài giây để vị trí cập nhật. |
| Thợ không thấy cuốc | Kiểm tra: thợ đã được duyệt, đang online, có vị trí (dòng "Vị trí của bạn" hiện số), trong 5 km, và trong 20 giây đầu chỉ 3 thợ xếp hạng cao nhất mới thấy. |
| Đăng nhập xong bị đẩy ra màn hình đăng nhập | Token hết hạn sau 24 giờ hoặc server khởi động lại với `JWT_SECRET` khác. Đăng nhập lại. |

## Cấu trúc mã nguồn

```
App.js                      Điểm vào: bọc AuthProvider + điều hướng
src/config.js               Địa chỉ server và kiểu bản đồ (sửa ở đây)
src/api/                    client.js (gọi HTTP, xử lý lỗi, token) · index.js (mỗi API một hàm)
src/auth/AuthContext.js     Đăng nhập/đăng ký/đăng xuất, lưu phiên bằng SecureStore
src/tho/ThoContext.js       Trạng thái online của thợ + tự gửi vị trí 5 giây/lần
src/navigation/             Chọn bộ màn hình theo vai trò (khách / thợ)
src/screens/                Màn hình: đăng nhập, đăng ký, khach/*, tho/*, admin/*
src/components/             Nút, ô nhập, thẻ, hộp thoại lý do; BanDo (chọn BanDoOSM hoặc BanDoNative)
src/utils/                  Nhãn trạng thái, định dạng tiền/khoảng cách, lấy vị trí, mở Google Maps
```

Quy ước: tên gọi nghiệp vụ dùng tiếng Việt không dấu cho khớp backend (`cuoc`, `tho`, `viTri`); thông báo cho
người dùng là tiếng Việt có dấu. Trạng thái cuốc và quy tắc hủy/trả cuốc trong `utils/nhan.js` phải khớp máy trạng thái
của server (`TrangThaiCuoc.java`).
