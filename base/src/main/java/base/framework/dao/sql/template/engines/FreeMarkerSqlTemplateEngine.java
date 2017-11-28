package base.framework.dao.sql.template.engines;

import base.framework.dao.sql.template.ParamMap;
import base.framework.dao.sql.template.SqlResult;
import base.framework.dao.sql.template.SqlTemplate;
import base.framework.dao.sql.template.SqlTemplateEngine;
import base.utils.template.TemplateEngine;
import base.utils.template.TemplateEngineFactory;
import freemarker.template.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cl on 2017/6/7.
 * 基于FreeMarker模板实现的SQL模板引擎
 */
public class FreeMarkerSqlTemplateEngine extends PreparedSqlTemplateEngine implements SqlTemplateEngine {

    @Override
    public SqlResult make(SqlTemplate template, Map<String, Object> model) {
        // 预处理SQL模板
        String tpl = this.prepareTemplate(template);

        // 将自定义模板函数加入上下文
        Map<String, Object> ctx = new HashMap<String, Object>();
        ParamMap paramMap = new ParamMap("param");
        ctx.put("buildParam", new BuildParamMethod(paramMap));
        ctx.put("joinParam", new JoinParamMethod(paramMap));
        ctx.putAll(model);

        TemplateEngine engine = TemplateEngineFactory.getEngine("freemarker");
        String sql = engine.evaluate(tpl, ctx, true);

        return new SqlResult(sql, paramMap);
    }

    /**
     * 将#{xxx}替换成${buildParam(xxx)}
     */
    @Override
    protected String replaceParam(String param) {
        StringBuilder sb = new StringBuilder();
        sb.append("${");
        if (param.startsWith("joinParam")) {
            sb.append(param.substring(0, param.length() - 1)).append(")");
        } else {
            sb.append("buildParam(").append(param).append(")");
        }
        sb.append("}");

        return sb.toString();
    }

    /**
     * freemarker模板自定义函数：构建SQL参数占位符
     */
    private class BuildParamMethod implements TemplateMethodModelEx {

        private ParamMap paramMap;

        public BuildParamMethod(ParamMap paramMap) {
            this.paramMap = paramMap;
        }

        @Override
        public Object exec(List list) throws TemplateModelException {
            if (list.size() > 0) {
                Object val = list.get(0);
                if (val instanceof TemplateScalarModel) {
                    val = ((TemplateScalarModel) val).getAsString();
                } else if (val instanceof TemplateNumberModel) {
                    val = ((TemplateNumberModel) val).getAsNumber().doubleValue();
                }

                return paramMap.buildParam(val);
            }
            return null;
        }
    }

    /**
     * freemarker模板自定义函数：将集合参数通过分隔符连接成字符串，集合中的每个参数转换成占位符
     */
    private class JoinParamMethod implements TemplateMethodModelEx {

        private ParamMap paramMap;

        public JoinParamMethod(ParamMap paramMap) {
            this.paramMap = paramMap;
        }

        @Override
        public Object exec(List list) throws TemplateModelException {
            StringBuilder sb = new StringBuilder();
            if (list.size() == 2) {
                TemplateSequenceModel c = (TemplateSequenceModel) list.get(0);
                String separator = ((TemplateScalarModel) list.get(1)).getAsString();
                for (int i = 0; i < c.size(); i++) {
                    Object val = c.get(i);
                    if (val instanceof TemplateScalarModel) {
                        val = ((TemplateScalarModel) val).getAsString();
                    } else if (val instanceof TemplateNumberModel) {
                        val = ((TemplateNumberModel) val).getAsNumber().doubleValue();
                    }
                    sb.append(paramMap.buildParam(val));
                    if (i < c.size() - 1) {
                        sb.append(separator);
                    }
                }
            }
            return sb.toString();
        }

    }

}
