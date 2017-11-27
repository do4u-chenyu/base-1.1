package base.config;

import base.framework.web.interceptor.CommonInterceptor;
import base.framework.web.mvc.AbstractMvcConfigureAdapter;
import base.framework.web.mvc.FastJsonConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by cl on 2017/4/18.
 * SpringMVC配置
 */
@Configuration
public class SpringMvcConfig {

    /**
     * MVC配置初始化
     */
    @Bean
    public WebMvcConfigurerAdapter mvcConfig() {
        return new AbstractMvcConfigureAdapter() {
            @Override
            public void init() {
                // 增加拦截器
                this.addInterceptor(new CommonInterceptor(), "/**");

                // 增加fastjson
                this.addMessageConverter(new FastJsonConverter());
            }
        };
    }

}
