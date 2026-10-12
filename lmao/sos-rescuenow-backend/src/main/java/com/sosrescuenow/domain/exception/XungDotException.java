package com.sosrescuenow.domain.exception;

/** Xung dot du lieu: co nguoi khac vua thay doi (vi du da co tho khac nhan cuoc). */
public class XungDotException extends DomainException {

    public XungDotException(String thongBao) {
        super("XUNG_DOT", thongBao);
    }
}
