package com.sosrescuenow.infrastructure.config;

import com.sosrescuenow.domain.dieuphoi.ChamDiemTongHop;
import com.sosrescuenow.domain.dieuphoi.ChienLuocChamDiem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;

@Configuration
@EnableScheduling
public class AppConfig {

    /** Dong ho UTC dung chung. Inject vao service de test co the thay bang dong ho co dinh. */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    /** Chien luoc cham diem tho dang dung. Doi sang ChamDiemGanNhat o day neu muon so sanh. */
    @Bean
    public ChienLuocChamDiem chienLuocChamDiem() {
        return new ChamDiemTongHop();
    }
}
