package icu.sunnyc.minio.example.utils;

import icu.sunnyc.minio.example.config.MinioConfig;
import io.minio.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * minio 操作工具类
 * @author hc
 * @date Created in 2022/9/3 22:06
 * @modified
 */
@Slf4j
@Component
public class MinioUtil {

    private final MinioClient minioClient;

    private final MinioConfig minioConfig;

    public MinioUtil(MinioClient minioClient, MinioConfig minioConfig) {
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
    }

    /**
     * 获取所有存储桶信息
     *
     * @return 所有存储桶集合
     */
    @SneakyThrows
    public List<Bucket> listBuckets() {
        return minioClient.listBuckets();
    }

    /**
     * 获取所有存储桶名称
     *
     * @return 所有存储桶名称集合
     */
    @SneakyThrows
    public List<String> listBucketNames() {
        return minioClient.listBuckets()
                .stream()
                .map(Bucket::name)
                .collect(Collectors.toList());
    }

    /**
     * 判断存储桶是否存在
     *
     * @param bucketName 存储桶名称
     * @return 是否存在
     */
    @SneakyThrows
    public boolean bucketExists(String bucketName) {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());
        String foundTip = found ? "已存在" : "不存在";
        log.info("存储桶 {} {}", bucketName, foundTip);
        return found;
    }

    /**
     * 创建存储桶
     *
     * @param bucketName 存储桶名称
     * @return 是否创建成功
     */
    @SneakyThrows
    public boolean makeBucket(String bucketName) {
        boolean found = bucketExists(bucketName);
        // 只创建不存在的存储桶
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
            return true;
        }
        return false;
    }

    /**
     * 列出存储桶中所有对象
     *
     * @param bucketName 存储桶名称
     * @return 存储桶所有对象集合，若该存储桶不存在，则返回 null
     */
    public Iterable<Result<Item>> listObjects(String bucketName) {
        boolean found = bucketExists(bucketName);
        if (found) {
            return minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .build());
        }
        return null;
    }

    /**
     * 列出存储桶中所有对象名称
     *
     * @param bucketName 存储桶名称
     * @return 存储桶中所有对象集合，若该存储桶不存在，则返回空 List
     */
    @SneakyThrows
    public List<String> listObjectsName(String bucketName) {
        boolean found = bucketExists(bucketName);
        List<String> bucketNameList = new ArrayList<>();
        if (found) {
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .build());
            for (Result<Item> result : results) {
                Item item = result.get();
                bucketNameList.add(item.objectName());
            }
        }
        return bucketNameList;
    }

    /**
     * 删除存储桶
     *
     * @param bucketName 存储桶名称
     * @return 是否删除成功
     */
    @SneakyThrows
    public boolean removeBucket(String bucketName) {
        boolean found = bucketExists(bucketName);
        // 如果存储桶不存在，那就没必要执行删除操作了
        if (!found) {
            return false;
        }
        List<String> bucketNameList = listObjectsName(bucketName);
        // 如果存储桶不为空，就不能删除此存储桶
        if (!bucketNameList.isEmpty()) {
            log.warn("存储桶 {} 不为空，无法删除！", bucketName);
            return false;
        }
        // 为空则继续执行删除操作
        minioClient.removeBucket(RemoveBucketArgs.builder()
                .bucket(bucketName)
                .build());
        // 删除完毕后检验是否真正的删除了
        return !bucketExists(bucketName);
    }

    /**
     * 获取对象元数据
     *
     * @param buketName 存储桶名称
     * @param objectName 对象名称
     * @return 对象元数据信息
     */
    @SneakyThrows
    public StatObjectResponse statObject(String buketName, String objectName) {
        boolean found = bucketExists(buketName);
        if (found) {
            return minioClient.statObject(StatObjectArgs.builder()
                            .bucket(buketName)
                            .object(objectName)
                    .build());
        }
        return null;
    }

    /**
     * 通过输入流上传文件，可指定存储桶，存储桶不存在会自动创建
     *
     * @param bucketName 存储此文件的存储桶名称
     * @param inputStream 文件输入流
     * @param objectName 存储至minio中的对象名称
     * @param contentType 文件类型
     * @return 是否上传成功
     */
    @SneakyThrows
    public boolean putObject(String bucketName, InputStream inputStream, String objectName, String contentType) {
        boolean found = bucketExists(bucketName);
        if (!found && !makeBucket(bucketName)) {
            log.error("存储桶 {} 不存在，并且创建失败！", bucketName);
            return false;
        }
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(inputStream, -1, minioConfig.getPartSize())
                .contentType(contentType)
                .build());
        StatObjectResponse statObjectResponse = statObject(bucketName, objectName);
        return statObjectResponse != null && statObjectResponse.size() > 0;
    }

    /**
     * 文件上传，可指定存储桶，存储桶不存在会自动创建
     *
     * @param bucketName 存储此文件的存储桶名称
     * @param file 要上传的文件
     * @param objectName 存储至minio中的对象名称
     * @param contentType 文件类型
     * @return 是否上传成功
     */
    @SneakyThrows
    public boolean putObject(String bucketName, MultipartFile file, String objectName, String contentType) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes());
        return putObject(bucketName, inputStream, objectName, contentType);
    }

    /**
     * 删除对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     */
    @SneakyThrows
    public boolean removeObject(String bucketName, String objectName) {
        boolean found = bucketExists(bucketName);
        if (found) {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            return true;
        }
        return false;
    }

}
