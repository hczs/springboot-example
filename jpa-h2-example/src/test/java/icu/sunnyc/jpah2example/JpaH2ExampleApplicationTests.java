package icu.sunnyc.jpah2example;

import icu.sunnyc.jpah2example.entity.UserEntity;
import icu.sunnyc.jpah2example.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@SpringBootTest(classes = JpaH2ExampleApplication.class)
class JpaH2ExampleApplicationTests {

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testAll() {
        // 新增数据
        UserEntity entity = userRepository.save(new UserEntity("阿牛" + UUID.randomUUID(), LocalDateTime.now(), "新增测试"));
        log.info("新增结果: {}", entity);

        // 修改数据 默认 save 按照 id 修改的
        entity = userRepository.save(new UserEntity(1L, "哈哈哈哈", LocalDateTime.now(), "修改以后的结果"));
        log.info("按照id修改结果: {}", entity);

        // 按照id查询
        Optional<UserEntity> optional = userRepository.findById(1L);
        optional.ifPresent(x -> log.info("按照id查询结果: {}", x));

        // 查询所有
        List<UserEntity> allUser = userRepository.findAll();
        log.info("查询所有数据内容如下");
        allUser.forEach(user -> log.info("item:{}", user));
        log.info("查询所有结果总数：{}", allUser.size());

        // 删除数据
        userRepository.deleteById(1L);

        // 删除id=1的数据以后
        allUser = userRepository.findAll();
        log.info("删除id=1的数据以后，查询内容如下");
        allUser.forEach(user -> log.info("item:{}", user));
        log.info("删除id=1的数据以后查询结果总数：{}", allUser.size());
    }


    @Test
    void getApplicationYml() {
        String userDir = System.getProperty("user.dir");
        log.info("userDir: {}", userDir);
        log.info("dataSourceUrl: {}", dataSourceUrl);
    }

}
