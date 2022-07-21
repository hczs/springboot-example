package icu.sunnyc.jpah2example.repository;

import icu.sunnyc.jpah2example.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author houcheng
 * @version V1.0
 * @date 2022/7/20 15:29:39
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
