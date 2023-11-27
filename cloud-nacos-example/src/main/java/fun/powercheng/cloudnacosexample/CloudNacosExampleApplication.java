package fun.powercheng.cloudnacosexample;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author hczs8
 */
@SpringBootApplication
public class CloudNacosExampleApplication {


    public static void main(String[] args) throws NacosException {
        Logger log = LoggerFactory.getLogger(CloudNacosExampleApplication.class);
        ConfigurableApplicationContext context = SpringApplication.run(CloudNacosExampleApplication.class, args);
        NacosConfigManager nacosConfigManager = context.getBean(NacosConfigManager.class);

        String dataId = "cloud-nacos-example";
        String group = "DEFAULT_GROUP";
        AbstractListener configListener = new AbstractListener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                log.info("监听到配置文件更改，文件 dataId: {} 最新配置文件内容：{}", dataId, configInfo);
                if ("test".equals(configInfo)) {
                    throw new RuntimeException("test ex");
                }
            }
        };
        nacosConfigManager.getConfigService().getConfigAndSignListener(dataId, group, 1000, configListener);
    }


}
