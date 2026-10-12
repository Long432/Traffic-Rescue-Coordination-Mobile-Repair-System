# SOS RescueNow - kiem thu tu dong luong SOS qua API (Module 3)
# Cach chay (server dang chay o cua so khac):
#     .\scripts\test-luong-sos.ps1
#     .\scripts\test-luong-sos.ps1 -AdminSdt 0900000099 -AdminMatKhau admin12345   # tu duyet tho bang API admin
#     .\scripts\test-luong-sos.ps1 -TestHetHan     # them kich ban "khong co tho" (cho ~2 phut)
# (Tai khoan admin duoc tao khi chay backend voi bien moi truong ADMIN_SDT va ADMIN_MAT_KHAU.)
# Noi dung script de ASCII (khong dau) de PowerShell 5.1 doc dung moi bang ma.

param(
    [string]$Base = "http://localhost:8080",
    [string]$AdminSdt = "",
    [string]$AdminMatKhau = "",
    [switch]$TestHetHan
)
$ErrorActionPreference = "Stop"

$ViDoKhach = 10.7769
$KinhDoKhach = 106.7009

function Goi {
    param([string]$Method, [string]$Path, $Body = $null, [string]$Token = $null, [switch]$ChoPhepLoi)
    $headers = @{}
    if ($Token) { $headers["Authorization"] = "Bearer $Token" }
    $params = @{ Method = $Method; Uri = "$Base$Path"; Headers = $headers }
    if ($null -ne $Body) {
        $params["ContentType"] = "application/json; charset=utf-8"
        $params["Body"] = [Text.Encoding]::UTF8.GetBytes(($Body | ConvertTo-Json -Depth 5))
    }
    elseif ($Method -eq "POST" -or $Method -eq "PUT") {
        # Windows PowerShell 5.1 tu gan "application/x-www-form-urlencoded" cho POST khong co noi dung;
        # chi dinh ro JSON de server doc dung.
        $params["ContentType"] = "application/json"
    }
    try {
        return Invoke-RestMethod @params
    }
    catch {
        $status = ""
        $text = ""
        if ($_.Exception.Response) { $status = [int]$_.Exception.Response.StatusCode }
        if ($_.ErrorDetails -and $_.ErrorDetails.Message) {
            $text = $_.ErrorDetails.Message
        }
        elseif ($_.Exception.Response) {
            try {
                $sr = New-Object IO.StreamReader($_.Exception.Response.GetResponseStream())
                $text = $sr.ReadToEnd()
            } catch { }
        }
        if ($ChoPhepLoi) {
            Write-Host ("   -> Server tu choi dung nhu mong doi: HTTP {0} {1}" -f $status, $text) -ForegroundColor Yellow
            return $null
        }
        throw ("HTTP {0}: {1}" -f $status, $text)
    }
}

function Buoc([string]$mota) { Write-Host ""; Write-Host "== $mota" -ForegroundColor Cyan }
function Ok([string]$mota) { Write-Host "   OK: $mota" -ForegroundColor Green }

# ---------------------------------------------------------------- 1. Dang ky
Buoc "1. Dang ky 1 khach va 1 tho"
$sdtKhach = "09" + (Get-Random -Minimum 10000000 -Maximum 99999999)
$sdtTho = "09" + (Get-Random -Minimum 10000000 -Maximum 99999999)
$khach = Goi POST "/api/auth/dang-ky" @{ hoTen = "Khach Test"; soDienThoai = $sdtKhach; matKhau = "matkhau123"; vaiTro = "USER" }
$tho = Goi POST "/api/auth/dang-ky" @{ hoTen = "Tho Test"; soDienThoai = $sdtTho; matKhau = "matkhau123"; vaiTro = "THO" }
Ok ("Khach id={0}, Tho id={1}" -f $khach.nguoiDungId, $tho.nguoiDungId)

# ---------------------------------------------------------------- 2. Duyet tho
Buoc "2. Duyet ho so tho (viec cua admin)"
if ($AdminSdt -ne "" -and $AdminMatKhau -ne "") {
    $admin = Goi POST "/api/auth/dang-nhap" @{ soDienThoai = $AdminSdt; matKhau = $AdminMatKhau }
    $cho = Goi GET "/api/admin/tho?chiChuaDuyet=true" $null $admin.accessToken
    Ok ("Admin thay {0} tho cho duyet" -f @($cho).Count)
    Goi POST ("/api/admin/tho/{0}/duyet" -f $tho.nguoiDungId) $null $admin.accessToken | Out-Null
    Ok ("Admin da duyet tho {0} (cho lam moi loai su co)" -f $tho.nguoiDungId)
}
else {
    Write-Host "   Khong co tai khoan admin: duyet tay bang SQL. Mo SSMS, mo file database\04_duyet_tho_thu_nghiem.sql, sua dong"
    Write-Host ("       DECLARE @thoId INT = {0};" -f $tho.nguoiDungId) -ForegroundColor Yellow
    Write-Host "   roi bam F5. Xong quay lai day."
    Read-Host "   Nhan Enter sau khi da chay SQL"
}

# ---------------------------------------------------------------- 3. Tho online
Buoc "3. Tho bat online va gui vi tri (cach khach khoang 500 m)"
Goi PUT "/api/tho/trang-thai" @{ online = $true } $tho.accessToken | Out-Null
Goi POST "/api/tho/vi-tri" @{ latitude = ($ViDoKhach + 0.0045); longitude = $KinhDoKhach } $tho.accessToken | Out-Null
Ok "Tho da online va co vi tri"

# ---------------------------------------------------------------- 4. Khach gui SOS
Buoc "4. Khach xem loai su co va gui SOS"
$loai = Goi GET "/api/loai-su-co" $null $khach.accessToken
$loai | ForEach-Object { Write-Host ("   [{0}] {1} - {2} VND" -f $_.id, $_.ten, $_.giaCoBan) }
$sos = Goi POST "/api/cuoc" @{ loaiSuCoId = 1; latitude = $ViDoKhach; longitude = $KinhDoKhach; diaChi = "Nga tu thu nghiem"; ghiChu = "Thung lop sau" } $khach.accessToken
$cuocId = $sos.cuoc.id
Ok ("Cuoc {0}, trang thai {1}, gia uoc tinh {2}, so tho phu hop {3} (mong doi: 1)" -f $cuocId, $sos.cuoc.trangThai, $sos.cuoc.giaUocTinh, $sos.soThoPhuHop)

Buoc "5. Khach gui SOS lan 2 khi dang co cuoc -> phai bi tu choi (409)"
Goi POST "/api/cuoc" @{ loaiSuCoId = 1; latitude = $ViDoKhach; longitude = $KinhDoKhach } $khach.accessToken -ChoPhepLoi | Out-Null

# ---------------------------------------------------------------- 6. Tho xem va nhan
Buoc "6. Tho xem cuoc quanh day"
$ds = Goi GET "/api/tho/cuoc-quanh-toi" $null $tho.accessToken
Ok ("Tho thay {0} cuoc; cuoc dau: id={1}, cach {2} km" -f @($ds).Count, $ds[0].cuoc.id, $ds[0].khoangCachKm)

Buoc "7. Tho nhan cuoc, roi thu nhan lai (phai bi tu choi)"
$nhan = Goi POST "/api/tho/cuoc/$cuocId/nhan" $null $tho.accessToken
Ok ("Trang thai: {0}, thoId={1}" -f $nhan.trangThai, $nhan.thoId)
Goi POST "/api/tho/cuoc/$cuocId/nhan" $null $tho.accessToken -ChoPhepLoi | Out-Null

# ---------------------------------------------------------------- 8. Tien do
Buoc "8. Tho di chuyen: DANG_DEN"
$r = Goi POST "/api/tho/cuoc/$cuocId/trang-thai" @{ trangThai = "DANG_DEN" } $tho.accessToken
Ok $r.trangThai

Buoc "9. Bam DA_DEN khi con cach 500 m -> phai bi tu choi (kiem tra check-in 100 m)"
Goi POST "/api/tho/cuoc/$cuocId/trang-thai" @{ trangThai = "DA_DEN" } $tho.accessToken -ChoPhepLoi | Out-Null

Buoc "10. Tho toi noi (gui vi tri trung khach) roi hoan tat cac buoc"
Goi POST "/api/tho/vi-tri" @{ latitude = $ViDoKhach; longitude = $KinhDoKhach } $tho.accessToken | Out-Null
foreach ($tt in @("DA_DEN", "DANG_SUA", "CHO_THANH_TOAN", "HOAN_THANH")) {
    $r = Goi POST "/api/tho/cuoc/$cuocId/trang-thai" @{ trangThai = $tt } $tho.accessToken
    Ok ("{0}  (tongTien={1})" -f $r.trangThai, $r.tongTien)
}

Buoc "11. Khach xem ket qua cuoi"
$cuoi = Goi GET "/api/cuoc/$cuocId" $null $khach.accessToken
Ok ("Trang thai: {0}, tong tien: {1}" -f $cuoi.trangThai, $cuoi.tongTien)

Buoc "12. Khach danh gia tho"
Goi POST "/api/cuoc/$cuocId/danh-gia" @{ soSao = 6 } $khach.accessToken -ChoPhepLoi | Out-Null
$dg = Goi POST "/api/cuoc/$cuocId/danh-gia" @{ soSao = 4; nhanXet = "Tho nhanh, lich su" } $khach.accessToken
Ok ("Da danh gia {0} sao" -f $dg.soSao)
Goi POST "/api/cuoc/$cuocId/danh-gia" @{ soSao = 5 } $khach.accessToken -ChoPhepLoi | Out-Null
$hoSo = Goi GET "/api/tho/ho-so" $null $tho.accessToken
Ok ("Diem trung binh cua tho: {0} (mong doi: 4)" -f $hoSo.diemTrungBinh)

# ---------------------------------------------------------------- 12. Huy
Buoc "13. Khach gui SOS moi roi huy"
$sos2 = Goi POST "/api/cuoc" @{ loaiSuCoId = 2; latitude = $ViDoKhach; longitude = $KinhDoKhach } $khach.accessToken
$huy = Goi POST "/api/cuoc/$($sos2.cuoc.id)/huy" @{ lyDo = "Da tu sua duoc" } $khach.accessToken
Ok ("Trang thai: {0}, ly do: {1}" -f $huy.trangThai, $huy.lyDoHuy)

Buoc "14. Khach xem lich su cuoc cua minh"
$lichSu = Goi GET "/api/cuoc/cua-toi" $null $khach.accessToken
$lichSu | ForEach-Object { Write-Host ("   cuoc {0}: {1}" -f $_.id, $_.trangThai) }

# ---------------------------------------------------------------- 14. Het han (tuy chon)
if ($TestHetHan) {
    Buoc "15. Kich ban khong co tho: tat online, gui SOS, doi 130 giay"
    Goi PUT "/api/tho/trang-thai" @{ online = $false } $tho.accessToken | Out-Null
    $sos3 = Goi POST "/api/cuoc" @{ loaiSuCoId = 1; latitude = $ViDoKhach; longitude = $KinhDoKhach } $khach.accessToken
    Ok ("Cuoc {0} dang tim, so tho phu hop = {1} (mong doi: 0)" -f $sos3.cuoc.id, $sos3.soThoPhuHop)
    Start-Sleep -Seconds 130
    $c3 = Goi GET "/api/cuoc/$($sos3.cuoc.id)" $null $khach.accessToken
    Ok ("Sau 130 giay, trang thai: {0} (mong doi: KHONG_CO_THO)" -f $c3.trangThai)
}

Write-Host ""
Write-Host "HOAN TAT: luong SOS chay dung." -ForegroundColor Green
