package base.config;

import base.framework.security.shiro.DefaultPasswordEncryptor;
import base.framework.security.shiro.ExtShiroFilterFactoryBean;
import base.framework.security.shiro.PasswordEncryptor;
import base.framework.security.shiro.ShiroConfiguration;
import base.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

/**
 * Created by cl on 2017/10/26.
 * shiro配置
 */
@Configuration
public class ShiroConfig {

    private final static Log logger = LogFactory.getLog(ShiroConfig.class);

    @Autowired
    private AuthorizingRealm realm;

    @Autowired(required = false)
    private CredentialsMatcher credentialsMatcher;

    private ShiroConfiguration config;

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter() {
        ShiroConfiguration cfg = this.getConfig();

        try {
            ExtShiroFilterFactoryBean factoryBean = new ExtShiroFilterFactoryBean(cfg);
            factoryBean.createDefaultSecurityManager(realm, credentialsMatcher);
            return factoryBean;
        } catch (Exception e) {
            logger.error("ShiroFilterFactoryBean初始化失败", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 密码加密器，用于创建用户时加密密码
     */
    @Bean
    @ConditionalOnMissingBean(PasswordEncryptor.class)
    public PasswordEncryptor passwordEncryptor() {
        ShiroConfiguration cfg = this.getConfig();
        ShiroConfiguration.PasswordEncryption pe = cfg.getPasswordEncryption();
        return new DefaultPasswordEncryptor(pe.algorithmName, pe.hashIterations, pe.toHex);
    }

    private ShiroConfiguration getConfig() {
        if (null == config) {
            try {
                InputStream in = CommonUtils.getClassPathResourceAsStream("shiro-config.xml");
                config = ShiroConfiguration.load(in);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
                throw new RuntimeException("shiro-config.xml配置文件加载失败");
            }
        }
        return config;
    }

}
