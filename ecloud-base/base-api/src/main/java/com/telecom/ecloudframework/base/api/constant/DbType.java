package com.telecom.ecloudframework.base.api.constant;

/**
 * <pre>
 * 数据库类型的枚举
 * </pre>
 *
 * @author aschs
 */
public enum DbType {
    /**
     * MYSQL
     */
    MYSQL("mysql", "mysql数据库", "com.mysql.jdbc.Driver", "jdbc:mysql://主机:3306/数据库名?useUnicode=true&characterEncoding=utf-8"),
    /**
     * ORACLE
     */
    ORACLE("oracle", "oracle数据库", "oracle.jdbc.OracleDriver", "jdbc:oracle:thin:@主机:1521:数据库实例"),
    /**
     * 达梦数据库
     */
    DMSQL("dmsql", "达梦数据库", "dm.jdbc.driver.DmDriver", "jdbc:dm://主机:5326"),
    /**
     * DRDS数据源
     */
    DRDS("drds", "DRDS数据源", "com.mysql.jdbc.Driver", "jdbc:mysql://主机:3306/数据库名?useUnicode=true&characterEncoding=utf-8"),

    KINGBASE("kingbase", "人大金仓数据源", "com.kingbase8.Driver", "jdbc:kingbase8://主机:3306/数据库名?"),

    /**
     * SQLSERVER
     */
    SQLSERVER("sqlserver", "sqlserver数据库", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://主机:1433;databaseName=数据库名");


    private String key;
    private String desc;
    private String driverClassName;
    private String url;

    private DbType(String key, String desc, String driverClassName, String url) {
        this.desc = desc;
        this.key = key;
        this.driverClassName = driverClassName;
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public String getKey() {
        return key;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getUrl() {
        return url;
    }

    /**
     * <pre>
     * 根据key来判断是否跟当前一致
     * </pre>
     *
     * @param key
     * @return
     */
    public boolean equalsWithKey(String key) {
        return this.key.equals(key);
    }

    /**
     * 根据key获取泛型
     *
     * @param key
     * @return PermissionType
     * @throws @since 1.0.0
     */
    public static DbType getByKey(String key) {
        for (DbType value : DbType.values()) {
            if (value.key.equals(key)) {
                return value;
            }
        }
        return null;
    }
}
