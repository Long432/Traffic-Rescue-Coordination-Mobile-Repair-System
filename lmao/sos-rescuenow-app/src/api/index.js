import { goi } from './client';

// ---- Tai khoan ----
export const dangKy = (b) => goi('POST', '/api/auth/dang-ky', b);
export const dangNhap = (b) => goi('POST', '/api/auth/dang-nhap', b);

// ---- Khach hang ----
export const loaiSuCo = () => goi('GET', '/api/loai-su-co');
export const guiSos = (b) => goi('POST', '/api/cuoc', b);
export const cuocCuaToi = () => goi('GET', '/api/cuoc/cua-toi');
export const xemCuoc = (id) => goi('GET', `/api/cuoc/${id}`);
export const huyCuoc = (id, lyDo) => goi('POST', `/api/cuoc/${id}/huy`, { lyDo });
export const viTriThoCuaCuoc = (id) => goi('GET', `/api/cuoc/${id}/vi-tri-tho`);

// ---- Tho cuu ho ----
export const thoHoSo = () => goi('GET', '/api/tho/ho-so');
export const thoCuocHienTai = () => goi('GET', '/api/tho/cuoc-hien-tai');
export const thoDatOnline = (online) => goi('PUT', '/api/tho/trang-thai', { online });
export const thoGuiViTri = (latitude, longitude) => goi('POST', '/api/tho/vi-tri', { latitude, longitude });
export const thoCuocQuanhToi = () => goi('GET', '/api/tho/cuoc-quanh-toi');
export const thoNhanCuoc = (id) => goi('POST', `/api/tho/cuoc/${id}/nhan`);
export const thoChuyenTrangThai = (id, trangThai) => goi('POST', `/api/tho/cuoc/${id}/trang-thai`, { trangThai });
export const thoTraCuoc = (id, lyDo) => goi('POST', `/api/tho/cuoc/${id}/tra-cuoc`, { lyDo });

// ---- Danh gia ----
export const danhGiaCuoc = (id, soSao, nhanXet) => goi('POST', `/api/cuoc/${id}/danh-gia`, { soSao, nhanXet });
export const xemDanhGia = (id) => goi('GET', `/api/cuoc/${id}/danh-gia`);

// ---- Quan tri vien ----
export const adminDanhSachTho = (chiChuaDuyet) => goi('GET', `/api/admin/tho?chiChuaDuyet=${chiChuaDuyet ? 'true' : 'false'}`);
export const adminDuyetTho = (id) => goi('POST', `/api/admin/tho/${id}/duyet`);
