package base.framework.rpc.motan.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by cl on 2017/12/2.
 * BasicServiceConfigBean的配置
 */
@ConfigurationProperties(prefix = "motan.basicService")
public class BasicServiceProperties {

    /* 服务发布的方式，协议:端口号 */
    private String export;

    /* 服务发布的端口号 */
    private Integer exportPort;

    private String application;

    private String group;

    private String module;

    private String version;

    private Boolean accessLog;

    private Boolean shareChannel;

    public String getExport() {
        return export;
    }

    public void setExport(String export) {
        this.export = export;
    }

    public Integer getExportPort() {
        return exportPort;
    }

    public void setExportPort(Integer exportPort) {
        this.exportPort = exportPort;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Boolean getAccessLog() {
        return accessLog;
    }

    public void setAccessLog(Boolean accessLog) {
        this.accessLog = accessLog;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public Boolean getShareChannel() {
        return shareChannel;
    }

    public void setShareChannel(Boolean shareChannel) {
        this.shareChannel = shareChannel;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
