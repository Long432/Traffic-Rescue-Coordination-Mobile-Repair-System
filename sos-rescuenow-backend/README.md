# SOS RescueNow – Backend (xác thực · luồng SOS · đánh giá · admin)

Spring Boot 3.5 · Java 17+ · Maven · SQL Server (SSMS) · JWT

## 1. Chuẩn bị

1. **JDK 17 trở lên** và **Maven 3.9+** (`java -version`, `mvn -v`).
2. **Tạo CSDL**: mở `database/01_tao_csdl.sql` trong SSMS và nhấn F5.
3. **Bật TCP/IP cho SQL Server** (bắt buộc, vì Java kết nối qua mạng chứ không dùng Windows pipe):
   - Mở *SQL Server Configuration Manager* → *SQL Server Network Configuration* → *Protocols for ...* → bật **TCP/IP**.
   - Chuột phải TCP/IP → *Properties* → tab *IP Addresses* → kéo xuống **IPAll**: xóa *TCP Dynamic Ports*, đặt **TCP Port = 1433**.
   - Khởi động lại dịch vụ SQL Server. (Bản Express thường là instance tên `SQLEXPRESS`, dùng cổng động; đặt cổng 1433 như trên là cách đơn giản nhất.)
4. **Bật đăng nhập SQL**: trong SSMS, chuột phải server → *Properties* → *Security* → chọn *SQL Server and Windows Authentication mode*; sau đó *Security → Logins → sa* → đặt mật khẩu và *Enabled*. Khởi động lại dịch vụ.
   (Nên tạo user riêng thay vì dùng `sa` khi làm thật.)

## 2. Biến môi trường

| Biến | Ý nghĩa | Mặc định |
|---|---|---|
| `DB_PASSWORD` | Mật khẩu SQL Server | **bắt buộc** |
| `JWT_SECRET` | Khóa ký JWT, **tối thiểu 32 ký tự** | **bắt buộc** |
| `DB_USER` | Tài khoản SQL Server | `sa` |
| `DB_HOST` / `DB_PORT` / `DB_NAME` | Địa chỉ, cổng, tên CSDL | `localhost` / `1433` / `SosRescueNow` |

Sinh khóa JWT ngẫu nhiên (PowerShell):

```powershell
$b = New-Object byte[] 48; [System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($b); [Convert]::ToBase64String($b)
```

Đặt biến (PowerShell, chỉ hiệu lực trong cửa sổ hiện tại):

```powershell
$env:DB_PASSWORD = "mat-khau-sql-cua-ban"
$env:JWT_SECRET  = "chuoi-ngau-nhien-o-tren"
```

Trong IntelliJ: *Run → Edit Configurations → Environment variables*. **Không ghi mật khẩu/khóa vào file đã commit.**

## 3. Chạy

```bash
mvn test             # chay unit test
mvn spring-boot:run  # chay server tai http://localhost:8080
```

## 4. Thử API (Postman hoặc curl)

Đăng ký (201, trả token):

```bash
curl -X POST http://localhost:8080/api/auth/dang-ky -H "Content-Type: application/json" \
  -d '{"hoTen":"Nguyễn An","soDienThoai":"0912345678","matKhau":"matkhau123","vaiTro":"USER"}'
```

Đăng nhập: `POST /api/auth/dang-nhap` với `{"soDienThoai":"0912345678","matKhau":"matkhau123"}`.

Xem thông tin bản thân: `GET /api/me` với header `Authorization: Bearer <accessToken>`.

| Tình huống | Kết quả mong đợi |
|---|---|
| Đăng ký trùng số điện thoại | 409 `TRUNG_LAP` |
| Mật khẩu ngắn, số điện thoại sai định dạng | 400 `DU_LIEU_KHONG_HOP_LE` kèm `chiTiet` theo từng trường |
| Đăng ký `vaiTro: ADMIN` | 400 |
| Sai mật khẩu hoặc số điện thoại không tồn tại | 401 `SAI_THONG_TIN_DANG_NHAP` (cùng một thông báo) |
| Gọi `/api/me` không có token hoặc token sai | 401 `CHUA_XAC_THUC` |

Lưu ý: các tài khoản mẫu trong SQL có `MatKhauHash = 'CHO_BCRYPT'` nên **không đăng nhập được**; hãy tạo tài khoản qua API đăng ký.

## 5. Kiến trúc (Clean Architecture)

```
presentation/    Controller, DTO request/response, GlobalExceptionHandler   (HTTP)
application/     Use case (AuthService...), command/result, PORT (interface)
domain/          Nghiệp vụ thuần: NguoiDung, TrangThaiCuoc (state machine), exception
infrastructure/  Cài đặt cổng: JPA entity + repository + adapter, JWT, BCrypt, cấu hình bảo mật
```

**Quy tắc phụ thuộc:** `domain` không import gì từ các tầng khác; `application` chỉ biết `domain` và các interface trong `application.port`; `infrastructure` và `presentation` ở ngoài cùng. Muốn đổi cách lưu trữ hoặc cách sinh token, chỉ viết lại adapter, không đụng use case.

**Quy ước đặt tên:** lớp/bảng/cột nghiệp vụ dùng tiếng Việt không dấu khớp với CSDL (`CuocCuuHo`, `TrangThai`); thuật ngữ kỹ thuật giữ tiếng Anh (`Controller`, `Repository`, `Filter`). Thông báo cho người dùng viết tiếng Việt có dấu.

**Thêm một tính năng mới** (ví dụ gửi SOS): domain (quy tắc) → port (interface lưu trữ) → use case → adapter (JPA) → controller + DTO → test. Test use case bằng Mockito, không cần Spring hay CSDL.

## 6. Quyết định thiết kế cần biết

- **Schema do script SQL quản lý** (`ddl-auto: none`), không để Hibernate tự sửa bảng.
- **`PhysicalNamingStrategyStandardImpl`**: giữ nguyên tên bảng/cột. Nếu bỏ dòng này, Spring Boot đổi `NguoiDung` thành `nguoi_dung` và mọi truy vấn lỗi "Invalid object name".
- **Mọi mốc thời gian là UTC** (`LocalDateTime` + `Clock.systemUTC()`); đổi sang giờ địa phương ở app mobile khi hiển thị.
- **`@Service`/`@Transactional` đặt ngay ở tầng application** là sự thỏa hiệp thực dụng với Spring; logic vẫn không phụ thuộc web hay JPA.
- **Validation hai lớp**: định dạng đầu vào ở DTO (`@Valid`), quy tắc nghiệp vụ ở use case/domain.
- **Chưa làm** (để các module sau): kiểm tra tài khoản bị khóa ở mỗi request (hiện chỉ kiểm tra lúc đăng nhập, token vẫn sống tối đa 24 giờ), refresh token, giới hạn số lần đăng nhập sai.
- **Spring Boot 3.5** hết hỗ trợ mã nguồn mở từ 30/06/2026; dòng 4.x là bản mới hơn. Với đồ án thì 3.5 vẫn ổn và tài liệu phổ biến hơn; nếu nâng cấp lên 4.x cần đọc hướng dẫn migrate (có thay đổi về starter và Jackson).


---

## 7. Module 3 – Luồng SOS

### 7.1 Nâng cấp CSDL (làm một lần)

Module này thêm hai cột (`CuocCuuHo.BatDauTimLuc`, `LichSuTrangThai.GhiChu`). Với CSDL đang có dữ liệu:
mở `database\03_nang_cap_module3.sql` trong SSMS (đăng nhập **Windows Authentication**, vì tài khoản `long`
chỉ có quyền đọc/ghi dữ liệu, không có quyền sửa cấu trúc bảng) rồi nhấn F5. Kết quả phải là hai ô `OK`.
**Đừng** chạy lại `01_tao_csdl.sql` vì nó xóa sạch dữ liệu (file 01 đã được cập nhật cho cài đặt mới).

### 7.2 API

| Vai trò | Phương thức và đường dẫn | Việc làm |
|---|---|---|
| Ai cũng (đã đăng nhập) | `GET /api/loai-su-co` | Danh sách loại sự cố và giá cơ bản |
| USER | `POST /api/cuoc` | Gửi SOS `{loaiSuCoId, latitude, longitude, diaChi?, ghiChu?}` → 201, kèm `soThoPhuHop` |
| USER | `GET /api/cuoc/cua-toi` | 50 cuốc gần nhất của mình |
| USER, THO, ADMIN | `GET /api/cuoc/{id}` | Xem cuốc (chỉ chủ cuốc, thợ được giao, admin) |
| USER | `GET /api/cuoc/{id}/vi-tri-tho` | Tên và vị trí mới nhất của thợ đang phụ trách cuốc (204 nếu chưa có thợ hoặc cuốc đã kết thúc) |
| USER | `POST /api/cuoc/{id}/huy` | Hủy cuốc `{lyDo?}` (trước khi thợ bắt đầu sửa) |
| THO | `GET /api/tho/ho-so` | Hồ sơ của mình: online, đang bận, đã duyệt, điểm |
| THO | `GET /api/tho/cuoc-hien-tai` | Cuốc đang làm dở (204 nếu đang rảnh) — để app tiếp tục sau khi mở lại |
| THO | `PUT /api/tho/trang-thai` | Bật/tắt online `{online}` (cần hồ sơ đã được duyệt) |
| THO | `POST /api/tho/vi-tri` | Gửi vị trí `{latitude, longitude}` (app gọi 3-5 giây/lần) |
| THO | `GET /api/tho/cuoc-quanh-toi` | Cuốc đang tìm mà thợ được phép thấy lúc này, gần nhất trước |
| THO | `POST /api/tho/cuoc/{id}/nhan` | Nhận cuốc (an toàn khi nhiều thợ cùng bấm) |
| THO | `POST /api/tho/cuoc/{id}/trang-thai` | `{trangThai}`: DANG_DEN, DA_DEN, DANG_SUA, CHO_THANH_TOAN, HOAN_THANH |
| THO | `POST /api/tho/cuoc/{id}/tra-cuoc` | Trả cuốc `{lyDo}` (chỉ khi DA_NHAN hoặc DANG_DEN) |

Mã lỗi mới: **409** `XUNG_DOT` (đã có người nhận, bạn đang bận, khách đang có cuốc khác),
**409** `TRANG_THAI_KHONG_HOP_LE` (chuyển sai bước), **403** `KHONG_CO_QUYEN` (không phải cuốc/vai trò của bạn).
Thời gian trả về dạng ISO-8601 có hậu tố `Z` (UTC).

### 7.3 Các quy tắc nghiệp vụ

- **Phát cuốc theo thời gian tìm** (`QuyTacPhatCuoc`, không cần lưu thêm trạng thái):
  0–20 giây: chỉ 3 thợ xếp hạng cao nhất trong 5 km; 20–60 giây: mọi thợ rảnh trong 5 km;
  60–120 giây: mở rộng 10 km; từ 120 giây: job chạy mỗi 10 giây chuyển cuốc sang `KHONG_CO_THO`.
- **Xếp hạng thợ** (`ChamDiemTongHop`, mẫu Strategy): `0.5 × độ gần + 0.3 × (sao/5) + 0.2 × tỉ lệ nhận cuốc`.
  Đổi chiến lược ở `AppConfig.chienLuocChamDiem()` (có sẵn `ChamDiemGanNhat` để so sánh).
- **Nhận cuốc an toàn**: `UPDATE ... WHERE TrangThai = 'DANG_TIM'` trả về 1 dòng cho người thắng và 0 dòng cho
  người thua; thợ cũng được đánh dấu bận bằng một `UPDATE` có điều kiện.
- **Check-in**: chỉ bấm được `DA_DEN` khi vị trí mới nhất của thợ cách khách ≤ 100 m.
- **Vị trí thợ quá 5 phút** bị coi là mất tín hiệu: thợ không được xếp hạng và không nhận được cuốc.
- **Giá**: giá cơ bản của loại sự cố, cộng 20% từ 22:00 đến 05:00 giờ Việt Nam, làm tròn đến nghìn đồng.
- Mỗi khách chỉ có **một cuốc đang hoạt động** tại một thời điểm.
- Mọi lần đổi trạng thái đều ghi vào bảng `LichSuTrangThai` (kèm lý do nếu có).

### 7.4 Chạy thử

1. Làm 7.1, rồi khởi động server như cũ (`mvn spring-boot:run`).
2. Ở một cửa sổ PowerShell khác, trong thư mục dự án: `.\scripts\test-luong-sos.ps1`.
   Script tạo một khách và một thợ, **dừng lại để bạn duyệt thợ bằng `04_duyet_tho_thu_nghiem.sql`**, rồi chạy
   cả luồng từ gửi SOS đến hoàn thành, kể cả các ca bị từ chối. Thêm `-TestHetHan` để thử kịch bản không có thợ (chờ ~2 phút).
3. Kiểm chứng tranh chấp trên SQL Server thật (tùy chọn):
   `$env:SOS_IT = '1'; mvn test -Dtest=NhanCuocDongThoiIT` (10 thợ cùng nhận một cuốc, đúng một thợ thắng).
   Không đặt `SOS_IT` thì bài này tự bỏ qua.

### 7.5 Giới hạn đã biết (để các module sau)

- Thợ phải **hỏi lại** `cuoc-quanh-toi` để thấy cuốc mới; đẩy thông báo thời gian thực (WebSocket/FCM) là module sau.
- Vị trí thợ đang ghi thẳng vào SQL Server mỗi lần gọi; khi có nhiều thợ nên giữ vị trí mới nhất trong bộ nhớ và
  ghi xuống CSDL theo chu kỳ.
- Hai yêu cầu SOS của cùng một khách gửi **chính xác cùng lúc** vẫn có thể cùng vượt qua kiểm tra "đang có cuốc"
  (cần một chỉ mục duy nhất có điều kiện ở CSDL).
- Chưa có: khóa tài khoản khi hủy quá nhiều, điểm uy tín, vật tư phát sinh, đánh giá, tài khoản admin để duyệt thợ.
- Việc tìm thợ chạy một truy vấn cho mỗi cuốc đang tìm khi thợ mở danh sách; ổn với quy mô đồ án.

---

## 8. Ứng dụng di động

App React Native (Expo) nằm ở gói riêng `sos-rescuenow-app`, hướng dẫn cài và chạy trong `HUONG_DAN_APP.md`.
Điện thoại gọi các API ở mục 7.2, nên server cần mở cổng 8080 cho mạng nội bộ (Windows Firewall) — xem hướng dẫn app.


---

## 9. Module 5 – Đánh giá thợ và quản trị viên duyệt thợ

### 9.1 Tạo tài khoản quản trị viên

API đăng ký không cho tạo ADMIN, nên admin được tạo **lúc server khởi động** nếu có hai biến môi trường
(đặt trong cùng cửa sổ PowerShell chạy `mvn spring-boot:run`):

```powershell
$env:ADMIN_SDT = '0900000099'
$env:ADMIN_MAT_KHAU = 'admin12345'
```

Tài khoản chỉ được tạo nếu số điện thoại đó **chưa có** (không ghi đè mật khẩu của tài khoản đã có). Đừng dùng
`0900000001`: đó là tài khoản mẫu trong dữ liệu SQL với mật khẩu giữ chỗ nên không đăng nhập được. Mật khẩu 8–72 ký tự.
Không đặt hai biến này thì server bỏ qua bước tạo admin. Đổi mật khẩu mặc định khi nộp đồ án.

### 9.2 API mới

| Vai trò | Phương thức và đường dẫn | Việc làm |
|---|---|---|
| USER | `POST /api/cuoc/{id}/danh-gia` | `{soSao 1-5, nhanXet?}` → 201. Chỉ cuốc `HOAN_THANH` của chính mình, mỗi cuốc một lần |
| USER, THO, ADMIN | `GET /api/cuoc/{id}/danh-gia` | Xem đánh giá (204 nếu chưa có); chỉ chủ cuốc, thợ phụ trách, admin |
| ADMIN | `GET /api/admin/tho?chiChuaDuyet=true` | Danh sách thợ (mới nhất trước) kèm tên, số điện thoại, trạng thái, điểm |
| ADMIN | `POST /api/admin/tho/{id}/duyet` | Duyệt thợ; body tùy chọn `{loaiSuCoIds:[...]}`, bỏ trống = làm mọi loại sự cố |

### 9.3 Quy tắc

- Điểm của thợ (`DiemTrungBinh`) = **trung bình mọi đánh giá** khách đã cho thợ đó, làm tròn 2 chữ số; chưa có đánh giá thì giữ 5.00.
- Điểm này **đi thẳng vào công thức xếp hạng** (`0.3 × sao/5`), nên thợ bị đánh giá thấp sẽ xếp sau trong 20 giây ưu tiên đầu.
- Hai yêu cầu đánh giá cùng lúc cho một cuốc: ràng buộc UNIQUE ở CSDL (`UQ_DanhGia_Cuoc`) là hàng rào cuối, người đến sau nhận 409.
- Thợ chưa được duyệt không bật online được; sau khi admin duyệt thì bật được và nhận được cuốc thuộc các loại sự cố đã chọn.

### 9.4 Chạy thử

`.\scripts\test-luong-sos.ps1 -AdminSdt 0900000099 -AdminMatKhau admin12345` chạy cả luồng, trong đó admin duyệt thợ qua API
(không cần SQL) và khách đánh giá thợ ở cuối (kể cả các ca bị từ chối: 6 sao, đánh giá lần hai).

### 9.5 Giới hạn đã biết

- Cập nhật điểm trung bình tính lại từ mọi đánh giá của thợ; hai đánh giá cho **hai cuốc khác nhau** của cùng một thợ gửi
  chính xác cùng lúc có thể làm một lần cập nhật điểm bỏ sót đánh giá kia cho đến đánh giá kế tiếp.
- Chưa có: từ chối/khóa hồ sơ thợ, thợ xem danh sách đánh giá của mình, khóa tài khoản khách hủy quá nhiều.
