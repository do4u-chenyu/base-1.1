package base.framework.dao.sql.template.engines;

import base.framework.dao.sql.template.ParamMap;
import base.framework.dao.sql.template.SqlResult;
import base.framework.dao.sql.template.SqlTemplate;
import base.framework.dao.sql.template.SqlTemplateEngine;
import base.utils.template.TemplateEngine;
import base.utils.template.TemplateEngineFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cl on 2017/6/7.
 * 基于Groovy模板实现的SQL模板引擎
 */
public class GroovySqlTemplateEngine extends PreparedSqlTemplateEngine implements SqlTemplateEngine {

    @Override
    public SqlResult make(SqlTemplate template, Map<String, Object> model) {
        // 预处理SQL模板
        String tpl = this.prepareTemplate(template);

        // 构建模板执行上下文
        Map<String, Object> ctx = new HashMap<String, Object>();
        ParamMap paramMap = new ParamMap("param");
        ctx.put("paramMap", paramMap);
        ctx.putAll(model);

        TemplateEngine engine = TemplateEngineFactory.getEngine("groovy");
        String sql = engine.evaluate(tpl, ctx, true);

        return new SqlResult(sql, paramMap);
    }

    /**
     * 重写processTemplate方法，为模板增加闭包
     */
    @Override
    protected String processTemplate(String tpl) {
        tpl = super.processTemplate(tpl);

        // 增加闭包，构建SQL参数占位符
        tpl = "<% def buildParam = { val, paramMap -> return paramMap.buildParam(val); }; %> \n" + tpl;

        return tpl;
    }

    /**
     * 将#{xxx}替换成${buildParam(xxx, paramMap)}
     */
    @Override
    protected String replaceParam(String param) {
        return "${buildParam(" + param + ", paramMap)}";
    }

}
