package base.config;

import base.framework.rpc.motan.BasicServiceCondition;
import base.framework.rpc.motan.properties.BasicRefererProperties;
import base.framework.rpc.motan.properties.BasicServiceProperties;
import base.framework.rpc.motan.properties.ProtocolProperties;
import base.framework.rpc.motan.properties.RegistryProperties;
import base.utils.ObjectUtils;
import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.config.springsupport.*;
import com.weibo.api.motan.util.MotanSwitcherUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * Created by cl on 2017/12/2.
 * motan初始化配置
 */
@Configuration
@EnableConfigurationProperties({ProtocolProperties.class, RegistryProperties.class, BasicServiceProperties.class, BasicRefererProperties.class})
public class MotanConfig implements ApplicationListener {

    private final static String MOTAN_PROTOCOL_NAME = "_motan_protocol_motan_";

    private final static String MOTAN_REGISTRY_NAME = "_motan_registry_";

    /**
     * 注解类扫描包配置
     */
    @Bean
    public AnnotationBean annotationBean(@Value("${motan.annotation.package}") String scanPackage) {
        AnnotationBean annotationBean = new AnnotationBean();
        annotationBean.setPackage(scanPackage);
        return annotationBean;
    }

    /**
     * 协议配置
     */
    @Bean(name = MOTAN_PROTOCOL_NAME)
    public ProtocolConfigBean protocolConfigBean(ProtocolProperties prop) {
        ProtocolConfigBean config = new ProtocolConfigBean();
        ObjectUtils.copyProperties(prop, config);
        config.setDefault(null != prop.getDefault() ? prop.getDefault() : true);
        if (StringUtils.isEmpty(prop.getName())) {
            config.setName("motan");
        }
        return config;
    }

    /**
     * 注册中心配置
     */
    @Bean(name = MOTAN_REGISTRY_NAME)
    public RegistryConfigBean registryConfigBean(RegistryProperties prop) {
        RegistryConfigBean config = new RegistryConfigBean();
        ObjectUtils.copyProperties(prop, config);
        config.setRegProtocol(prop.getProtocol());
        return config;
    }

    /**
     * RPC服务的发布配置
     */
    @Bean
    @Conditional(BasicServiceCondition.class)
    public BasicServiceConfigBean basicServiceConfigBean(BasicServiceProperties prop) {
        BasicServiceConfigBean config = new BasicServiceConfigBean();
        ObjectUtils.copyProperties(prop, config);
        // 未设置export时，默认使用protocol的名字:端口号
        if (StringUtils.isEmpty(prop.getExport())) {
            config.setExport(MOTAN_PROTOCOL_NAME + ":" + prop.getExportPort());
        }
        config.setRegistry(MOTAN_REGISTRY_NAME);

        return config;
    }

    /**
     * RPC服务的引用配置
     */
    @Bean
    public BasicRefererConfigBean basicRefererConfigBean(BasicRefererProperties prop) {
        BasicRefererConfigBean config = new BasicRefererConfigBean();
        ObjectUtils.copyProperties(prop, config);
        config.setProtocol(MOTAN_PROTOCOL_NAME);
        config.setRegistry(MOTAN_REGISTRY_NAME);
        return config;
    }


    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
    }

}
