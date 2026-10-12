// Nhan tieng Viet, mau sac va dinh dang hien thi cho trang thai cuoc cuu ho.
import { mau } from '../theme';

export const TEN_TRANG_THAI = {
  DANG_TIM: 'Đang tìm thợ',
  DA_NHAN: 'Thợ đã nhận',
  DANG_DEN: 'Thợ đang đến',
  DA_DEN: 'Thợ đã đến nơi',
  DANG_SUA: 'Đang sửa chữa',
  CHO_THANH_TOAN: 'Chờ thanh toán',
  HOAN_THANH: 'Hoàn thành',
  HUY: 'Đã hủy',
  KHONG_CO_THO: 'Không tìm được thợ',
};

const MAU_TRANG_THAI = {
  DANG_TIM: mau.cam,
  DA_NHAN: mau.xanh,
  DANG_DEN: mau.xanh,
  DA_DEN: mau.xanh,
  DANG_SUA: mau.xanh,
  CHO_THANH_TOAN: mau.cam,
  HOAN_THANH: mau.xanhLa,
  HUY: mau.xam,
  KHONG_CO_THO: mau.loi,
};

export const TRANG_THAI_KET_THUC = ['HOAN_THANH', 'HUY', 'KHONG_CO_THO'];

export const tenTrangThai = (tt) => TEN_TRANG_THAI[tt] || tt;
export const mauTrangThai = (tt) => MAU_TRANG_THAI[tt] || mau.xam;
export const daKetThuc = (tt) => TRANG_THAI_KET_THUC.includes(tt);

/** Khach chi huy duoc truoc khi tho bat dau sua (khop may trang thai o server). */
export const khachCoTheHuy = (tt) => ['DANG_TIM', 'DA_NHAN', 'DANG_DEN', 'DA_DEN'].includes(tt);

/** Tho chi tra duoc cuoc truoc khi den noi. */
export const thoCoTheTra = (tt) => tt === 'DA_NHAN' || tt === 'DANG_DEN';

/** Nut hanh dong ke tiep cua tho theo trang thai hien tai. */
export const BUOC_TIEP_CUA_THO = {
  DA_NHAN: { nhan: 'Bắt đầu đi tới khách', den: 'DANG_DEN' },
  DANG_DEN: { nhan: 'Tôi đã đến nơi', den: 'DA_DEN' },
  DA_DEN: { nhan: 'Bắt đầu sửa', den: 'DANG_SUA' },
  DANG_SUA: { nhan: 'Sửa xong, tính tiền', den: 'CHO_THANH_TOAN' },
  CHO_THANH_TOAN: { nhan: 'Đã thu tiền mặt, hoàn thành', den: 'HOAN_THANH' },
};

/** 80000 -> "80.000 đ" (khong dung Intl de chay on dinh tren moi may). */
export function formatTien(giaTri) {
  if (giaTri === null || giaTri === undefined || Number.isNaN(Number(giaTri))) {
    return '—';
  }
  const chuoi = String(Math.round(Number(giaTri))).replace(/\B(?=(\d{3})+(?!\d))/g, '.');
  return `${chuoi} đ`;
}

/** 0.4 -> "400 m", 2.345 -> "2.3 km" */
export function formatKhoangCach(km) {
  if (km === null || km === undefined || Number.isNaN(Number(km))) {
    return '—';
  }
  return km < 1 ? `${Math.round(km * 1000)} m` : `${Number(km).toFixed(1)} km`;
}

export const SDT_HOP_LE = /^0\d{9}$/;
