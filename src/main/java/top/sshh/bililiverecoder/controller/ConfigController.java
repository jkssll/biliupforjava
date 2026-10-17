package top.sshh.bililiverecoder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.sshh.bililiverecoder.service.impl.UploadRateLimitService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private UploadRateLimitService uploadRateLimitService;

    @GetMapping("/uploadRate")
    public Map<String, Object> getUploadRate() {
        Map<String, Object> result = new HashMap<>();
        result.put("limitKb", uploadRateLimitService.getLimitKb());
        return result;
    }

    /**
     * 保存全局上传限速，0 表示不限速
     *
     * @param limitKb 限速值，单位 KB/s
     */
    @PostMapping("/uploadRate")
    public Map<String, Object> saveUploadRate(@RequestParam(value = "limitKb", defaultValue = "0") int limitKb) {
        Map<String, Object> result = new HashMap<>();
        result.put("limitKb", uploadRateLimitService.saveLimitKb(limitKb));
        result.put("msg", "保存成功");
        return result;
    }
}
