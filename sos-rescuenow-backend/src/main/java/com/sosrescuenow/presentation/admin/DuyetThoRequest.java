package com.sosrescuenow.presentation.admin;

import java.util.Set;

/** loaiSuCoIds rong hoac khong gui: cho tho lam tat ca loai su co. */
public record DuyetThoRequest(Set<Integer> loaiSuCoIds) {
}
