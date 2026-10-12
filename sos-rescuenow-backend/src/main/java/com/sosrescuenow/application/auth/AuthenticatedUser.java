package com.sosrescuenow.application.auth;

import com.sosrescuenow.domain.model.VaiTro;

/** Nguoi dung da xac thuc cua yeu cau hien tai (rut ra tu JWT). */
public record AuthenticatedUser(Integer nguoiDungId, VaiTro vaiTro) {
}
