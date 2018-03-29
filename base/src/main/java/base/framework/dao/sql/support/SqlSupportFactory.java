package base.framework.dao.sql.support;

import base.framework.dao.sql.support.impl.MySQLSqlSupport;
import base.framework.dao.sql.support.impl.OracleSqlSupport;
import base.framework.dao.sql.support.impl.PostgreSQLSqlSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cl on 2017/4/12.
 * SQL语句支持工厂
 */
public class SqlSupportFactory {

    private final static String TYPE_ORACLE = "Oracle";
    private final static String TYPE_MYSQL = "MySQL";
    private final static String TYPE_POSTGRESQL = "PostgreSQL";
    private final static String TYPE_H2 = "H2";

    /**
     * 当前的默认数据库类型
     */
    private static String defaultType;

    private static Map<String, SqlSupport> supports = new HashMap<String, SqlSupport>();

    private SqlSupportFactory() {
    }

    static {
        registerSupport(TYPE_ORACLE, new OracleSqlSupport());
        registerSupport(TYPE_MYSQL, new MySQLSqlSupport());
        registerSupport(TYPE_POSTGRESQL, new PostgreSQLSqlSupport());
        registerSupport(TYPE_H2, new MySQLSqlSupport());
    }

    /**
     * 注册数据库SQL支持
     */
    public static void registerSupport(String type, SqlSupport support) {
        supports.put(type.toLowerCase(), support);
    }

    /**
     * 获取数据库SQL支持
     */
    public static SqlSupport getSupport(String type) {
        return supports.get(type.toLowerCase());
    }

    /**
     * 获取默认的SQL支持
     */
    public static SqlSupport getSupport() {
        // 未配置默认数据库类型时，默认Oracle
        String type = null != defaultType ? defaultType : TYPE_ORACLE;
        return getSupport(type);
    }

    /**
     * 设置当前默认的数据库类型
     */
    public static void setDefaultType(String type) {
        defaultType = type;
    }

}
