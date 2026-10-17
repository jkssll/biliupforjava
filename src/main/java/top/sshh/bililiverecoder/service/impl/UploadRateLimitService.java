package top.sshh.bililiverecoder.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.sshh.bililiverecoder.entity.SysConfig;
import top.sshh.bililiverecoder.repo.SysConfigRepository;
import top.sshh.bililiverecoder.util.UploadRateLimiter;

import java.util.Optional;

/**
 * 全局上传限速配置
 */
@Slf4j
@Service
public class UploadRateLimitService {

    private static final String CONFIG_KEY = "upload.rate.limit.kb";

    private static final int MAX_LIMIT_KB = 1024 * 1024;

    @Autowired
    private SysConfigRepository sysConfigRepository;

    @PostConstruct
    public void init() {
        int limitKb = 0;
        try {
            Optional<SysConfig> configOptional = sysConfigRepository.findById(CONFIG_KEY);
            if (configOptional.isPresent()) {
                limitKb = Integer.parseInt(configOptional.get().getConfigValue().trim());
            }
        } catch (Exception e) {
            log.error("读取全局上传限速配置失败，将使用不限速", e);
        }
        UploadRateLimiter.setLimitKb(limitKb);
        log.info("全局上传限速初始化完成，当前限速：{}", limitKb == 0 ? "不限速" : limitKb + " KB/s");
    }

    public int getLimitKb() {
        return UploadRateLimiter.getLimitKb();
    }

    /**
     * 保存全局限速，0 表示不限速
     *
     * @param limitKb 限速值，单位 KB/s
     * @return 实际保存的限速值
     */
    public synchronized int saveLimitKb(int limitKb) {
        if (limitKb < 0) {
            limitKb = 0;
        }
        if (limitKb > MAX_LIMIT_KB) {
            limitKb = MAX_LIMIT_KB;
        }
        SysConfig sysConfig = sysConfigRepository.findById(CONFIG_KEY).orElseGet(SysConfig::new);
        sysConfig.setConfigKey(CONFIG_KEY);
        sysConfig.setConfigValue(String.valueOf(limitKb));
        sysConfigRepository.save(sysConfig);
        UploadRateLimiter.setLimitKb(limitKb);
        log.info("全局上传限速已更新为：{}", limitKb == 0 ? "不限速" : limitKb + " KB/s");
        return limitKb;
    }
}
