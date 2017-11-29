package base.framework.dao.sql.template.engines;

import base.framework.dao.sql.template.SqlTemplate;
import base.framework.dao.sql.template.SqlTemplateEngine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cl on 2017/6/7.
 * SQL模板预处理的模板引擎，供子类继承
 */
public abstract class PreparedSqlTemplateEngine implements SqlTemplateEngine {

    protected Log logger = LogFactory.getLog(PreparedSqlTemplateEngine.class);

    /* 模板对象缓存容器 */
    private Map<String, Object> templateCache = new HashMap<String, Object>();

    /**
     * 获取预处理编译后的模板对象
     */
    protected Object getTemplate(SqlTemplate sqlTemplate) throws Exception {
        String id = sqlTemplate.getId();
        Object template = templateCache.get(id);

        if (null == template) {
            synchronized (templateCache) {
                template = templateCache.get(id);
                if (null == template) {
                    String tpl = sqlTemplate.getTpl();
                    logger.info("原始的SQL模板：" + tpl);
                    tpl = this.processTemplate(tpl);
                    logger.info("预处理后的SQL模板：" + tpl);

                    logger.info("创建模板对象");
                    template = this.createTemplate(tpl);
                    templateCache.put(id, template);
                }
            }
        }

        return template;
    }

    /**
     * 创建模板对象
     */
    protected abstract Object createTemplate(String tpl) throws Exception;

    /**
     * 处理模板，将模板中的#{xxx}中xxx取出替换成其他形式
     */
    protected String processTemplate(String tpl) {
        while (true) {
            int idx = tpl.indexOf("#{");
            if (idx == -1) {
                break;
            }

            for (int i = idx; i < tpl.length(); i++) {
                if (tpl.charAt(i) == '}') {
                    String param = tpl.substring(idx + 2, i).trim();
                    tpl = tpl.substring(0, idx) + this.replaceParam(param) + tpl.substring(i + 1);
                    break;
                }
            }
        }

        return tpl.trim();
    }

    /**
     * 替换#{xxx}部分
     */
    protected abstract String replaceParam(String param);

}
