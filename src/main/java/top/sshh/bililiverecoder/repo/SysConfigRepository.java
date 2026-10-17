package top.sshh.bililiverecoder.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import top.sshh.bililiverecoder.entity.SysConfig;

@Repository
public interface SysConfigRepository extends CrudRepository<SysConfig, String> {
}
