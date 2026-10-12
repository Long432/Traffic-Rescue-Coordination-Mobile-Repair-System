import { API_URL } from '../config';

const THOI_GIAN_CHO_MS = 15000;

/** Loi tra ve tu server (hoac mat ket noi). message da la tieng Viet, hien thi truc tiep duoc. */
export class ApiError extends Error {
  constructor(message, status, maLoi, chiTiet) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.maLoi = maLoi;
    this.chiTiet = chiTiet;
  }
}

let tokenHienTai = null;
let xuLyHetPhien = null;

export function datToken(token) {
  tokenHienTai = token;
}

/** Dang ky ham se duoc goi khi server tra 401 trong luc dang co phien (token het han). */
export function datXuLyHetPhien(fn) {
  xuLyHetPhien = fn;
}

/** Goi API: tra ve du lieu JSON, hoac null khi server tra 204. Nem ApiError khi that bai. */
export async function goi(phuongThuc, duongDan, body) {
  const dieuKhien = new AbortController();
  const hen = setTimeout(() => dieuKhien.abort(), THOI_GIAN_CHO_MS);
  const headers = { Accept: 'application/json' };
  if (body !== undefined) {
    headers['Content-Type'] = 'application/json';
  }
  if (tokenHienTai) {
    headers.Authorization = `Bearer ${tokenHienTai}`;
  }

  let res;
  try {
    res = await fetch(`${API_URL}${duongDan}`, {
      method: phuongThuc,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body),
      signal: dieuKhien.signal,
    });
  } catch (e) {
    const quaCham = e && e.name === 'AbortError';
    throw new ApiError(
      quaCham
        ? 'Máy chủ phản hồi quá chậm, hãy thử lại.'
        : 'Không kết nối được máy chủ. Kiểm tra mạng và địa chỉ API_URL trong src/config.js.',
      0,
      'MAT_KHONG_KET_NOI',
    );
  } finally {
    clearTimeout(hen);
  }

  if (res.status === 204) {
    return null;
  }
  let duLieu = null;
  const noiDung = await res.text();
  if (noiDung) {
    try {
      duLieu = JSON.parse(noiDung);
    } catch (e) {
      duLieu = null;
    }
  }
  if (!res.ok) {
    if (res.status === 401 && tokenHienTai && xuLyHetPhien) {
      xuLyHetPhien();
    }
    const thongBao = duLieu && duLieu.thongBao ? duLieu.thongBao : `Lỗi máy chủ (${res.status})`;
    throw new ApiError(thongBao, res.status, duLieu && duLieu.maLoi, duLieu && duLieu.chiTiet);
  }
  return duLieu;
}
