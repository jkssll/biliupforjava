package top.sshh.bililiverecoder.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * 系统配置
 */
@Data
@Entity
public class SysConfig {

    @Id
    private String configKey;

    private String configValue;
}
