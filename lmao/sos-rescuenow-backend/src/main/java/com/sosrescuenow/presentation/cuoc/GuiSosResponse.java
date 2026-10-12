package com.sosrescuenow.presentation.cuoc;

import com.sosrescuenow.application.cuoc.KetQuaGuiSos;

public record GuiSosResponse(CuocResponse cuoc, int soThoPhuHop) {

    public static GuiSosResponse tu(KetQuaGuiSos kq) {
        return new GuiSosResponse(CuocResponse.tu(kq.cuoc()), kq.soThoPhuHop());
    }
}
