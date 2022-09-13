package icu.sunnyc.minio.example;

import icu.sunnyc.minio.example.utils.MinioUtil;
import io.minio.Result;
import io.minio.StatObjectResponse;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MinioExampleApplicationTests {

    @Autowired
    private MinioUtil minioUtil;

    /**
     * 测试桶
     */
    private final String  testBucket = "test";

    /**
     * 测试对象
     */
    private final String  testObject = "hello_minio.txt";

    @Test
    @Order(1)
    void testMakeBucket() {
        boolean makeResult = minioUtil.makeBucket(testBucket);
        Assertions.assertTrue(makeResult, "测试创建存储桶");
    }

    @Test
    @Order(2)
    void testListBuckets() {
        List<Bucket> buckets = minioUtil.listBuckets();
        for (Bucket bucket : buckets) {
            System.out.println(bucket.creationDate() + ", " + bucket.name());
        }
        Assertions.assertNotNull(buckets, "测试查询所有桶对象");
    }

    @Test
    @Order(3)
    void testListBucketNames() {
        List<String> bucketNames = minioUtil.listBucketNames();
        bucketNames.forEach(System.out::println);
        Assertions.assertNotNull(bucketNames, "测试查询所有桶名称");
    }

    @Test
    @Order(4)
    void testBucketExists() {
        boolean found = minioUtil.bucketExists(testBucket);
        Assertions.assertTrue(found, "测试判断存储桶是否存在");
    }

    @Test
    @Order(5)
    void testPutObject() {
        String fileContent = "Hello MinIO";
        MockMultipartFile file = new MockMultipartFile("hello.txt", fileContent.getBytes(StandardCharsets.UTF_8));
        boolean result = minioUtil.putObject(testBucket, file, testObject);
        Assertions.assertTrue(result, "测试文件上传");
    }

    @Test
    @Order(6)
    void testListObjects() {
        Iterable<Result<Item>> results = minioUtil.listObjects(testBucket);
        results.forEach(item -> {
            try {
                System.out.println(item.get().objectName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        Assertions.assertNotNull(results, "测试查询指定存储桶中所有对象");
    }

    @Test
    @Order(7)
    void testStatObject() {
        StatObjectResponse statObjectResponse = minioUtil.statObject(testBucket, testObject);
        System.out.println(statObjectResponse);
        Assertions.assertNotNull(statObjectResponse, "测试获取对象元数据");
    }

    @Test
    @Order(8)
    void testGetObject() throws IOException {
        InputStream inputStream = minioUtil.getObject(testBucket, testObject);
        System.out.println(new String(IOUtils.toByteArray(inputStream), StandardCharsets.UTF_8));
        Assertions.assertNotNull(inputStream, "测试下载文件流");
    }

    @Test
    @Order(9)
    void testRemoveObject() {
        boolean result = minioUtil.removeObject(testBucket, testObject);
        Assertions.assertTrue(result, "测试删除对象");
    }

    @Test
    @Order(Integer.MAX_VALUE)
    void testRemoveBucket() {
        boolean result = minioUtil.removeBucket(testBucket);
        Assertions.assertTrue(result, "测试删除存储桶");
    }
}
