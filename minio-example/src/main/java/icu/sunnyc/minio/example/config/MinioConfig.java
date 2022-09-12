package icu.sunnyc.minio.example.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * minio 配置类
 * @author hc
 * @date Created in 2022/8/30 23:20
 * @modified
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    /**
     * minio 服务器地址
     */
    private String endpoint;

    /**
     * minio 账号
     */
    private String accessKey;

    /**
     * minio 密码
     */
    private String accessSecret;

    /**
     * 默认存储桶
     */
    private String defaultBucket;

    /**
     * 分块上传，每块的大小
     * 单位：字节（byte）
     */
    private Integer partSize;

    /**
     * 构造 minioClient bean 全局使用
     *
     * @return MinioClient
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, accessSecret)
                .build();
    }
}
