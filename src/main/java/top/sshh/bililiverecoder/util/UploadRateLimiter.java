package top.sshh.bililiverecoder.util;

import com.google.common.util.concurrent.RateLimiter;

/**
 * 全局上传限速器
 * 所有上传线路的分片上传共享同一个令牌桶，限速值为 0 时表示不限速
 */
public final class UploadRateLimiter {

    private static volatile RateLimiter limiter = null;

    private static volatile int limitKb = 0;

    private UploadRateLimiter() {
    }

    /**
     * 设置全局限速
     *
     * @param kb 限速值，单位 KB/s，0 表示不限速
     */
    public static void setLimitKb(int kb) {
        if (kb < 0) {
            kb = 0;
        }
        limitKb = kb;
        limiter = kb == 0 ? null : RateLimiter.create(kb * 1024.0);
    }

    public static int getLimitKb() {
        return limitKb;
    }

    /**
     * 申请上传字节数对应的令牌，限速为 0 时直接返回
     *
     * @param bytes 本次读取的字节数
     */
    public static void acquire(int bytes) {
        if (bytes <= 0) {
            return;
        }
        RateLimiter current = limiter;
        if (current != null) {
            current.acquire(bytes);
        }
    }
}
