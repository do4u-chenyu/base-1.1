package demo.config;

import base.config.*;
import base.utils.forward.HttpForwardManager;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * Created by cl on 2017/4/6.
 * 系统配置类，通过注解导入各个功能组件的配置
 */
@Configuration
@Import({
        JpaDaoConfig.class,
        RedisCacheConfig.class,
        EhCacheConfig.class,
        CacheServiceConfig.class,
        PacketTransferServiceConfig.class,
        ExtMvcAutoConfig.class,
//        SpringMvcConfig.class,
        SysUtilsConfig.class,
        CorsFilterConfig.class
})
public class SystemConfig implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 初始化HttpForwardManager
        HttpForwardManager.init();
    }

}
