package com.sosrescuenow.infrastructure.scheduling;

import com.sosrescuenow.application.cuoc.CuocService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Dinh ky chuyen cac cuoc tim tho qua 120 giay sang KHONG_CO_THO. */
@Component
public class HetHanCuocJob {

    private static final Logger log = LoggerFactory.getLogger(HetHanCuocJob.class);

    private final CuocService cuocService;

    public HetHanCuocJob(CuocService cuocService) {
        this.cuocService = cuocService;
    }

    @Scheduled(fixedDelayString = "PT10S", initialDelayString = "PT10S")
    public void chay() {
        try {
            int soCuoc = cuocService.xuLyCuocHetHan();
            if (soCuoc > 0) {
                log.info("Đã chuyển {} cuốc sang KHONG_CO_THO do hết thời gian tìm thợ", soCuoc);
            }
        } catch (RuntimeException e) {
            // Khong de loi cua mot lan chay lam dung lich: lan sau se thu lai.
            log.error("Job xử lý cuốc hết hạn bị lỗi", e);
        }
    }
}
