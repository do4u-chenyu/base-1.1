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

    /* 预处理的SQL模板缓存容器 */
    private Map<String, String> tplCache = new HashMap<String, String>();

    /**
     * 预处理SQL模板，将模板中的#{xxx}中xxx取出替换成其他形式（缓存预处理后的模板）
     */
    protected String prepareTemplate(SqlTemplate template) {
        String id = template.getId();
        String tpl = tplCache.get(id);

        if (null == tpl) {
            synchronized (tplCache) {
                tpl = tplCache.get(id);
                if (null == tpl) {
                    tpl = template.getTpl();
                    logger.info("原始的SQL模板：" + tpl);
                    tpl = this.processTemplate(tpl);
                    logger.info("预处理后的SQL模板：" + tpl);
                    tplCache.put(id, tpl);
                }
            }
        }

        return tpl;
    }

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
