package com.sosrescuenow.application.port;

public interface PasswordHasher {

    String bam(String matKhauGoc);

    boolean khop(String matKhauGoc, String matKhauDaBam);
}
