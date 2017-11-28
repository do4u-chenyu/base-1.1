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

        String closure = "";
        // 闭包buildParam：将参数转换成占位符
        closure += "<% def buildParam = { val, paramMap -> return paramMap.buildParam(val); }; %>";
        // 闭包joinParam：将集合参数通过分隔符连接成字符串，集合中的每个参数转换成占位符
        closure += "<% def joinParam = {collection, separator, paramMap -> " +
                "def list = []; " +
                "collection.each({ it -> list << buildParam(it, paramMap);}); " +
                "return list.join(separator); " +
                "}; %>";

        return closure + "\n" + tpl;
    }

    /**
     * 将#{xxx}替换成${xxx}的形式
     */
    @Override
    protected String replaceParam(String param) {
        StringBuilder sb = new StringBuilder();
        sb.append("${");
        if (param.startsWith("joinParam")) {
            sb.append(param.substring(0, param.length() - 1)).append(", paramMap)");
        } else {
            sb.append("buildParam(").append(param).append(", paramMap)");
        }
        sb.append("}");

        return sb.toString();
    }

}
