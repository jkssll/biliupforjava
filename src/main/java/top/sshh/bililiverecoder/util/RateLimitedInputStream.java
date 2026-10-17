package top.sshh.bililiverecoder.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 上传限速流
 * 读取时按照实际读取到的字节数向全局限速器申请令牌
 */
public class RateLimitedInputStream extends FilterInputStream {

    public RateLimitedInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        int b = super.read();
        if (b != -1) {
            UploadRateLimiter.acquire(1);
        }
        return b;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int n = super.read(b, off, len);
        if (n > 0) {
            UploadRateLimiter.acquire(n);
        }
        return n;
    }
}
